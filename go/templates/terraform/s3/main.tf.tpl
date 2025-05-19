provider "aws" {
  region     = "{{ .Region }}"
  access_key = "{{ .AWSAccessKey }}"
  secret_key = "{{ .AWSSecretKey }}"
}

resource "aws_s3_bucket" "{{ .ComponentId }}" {
  bucket = "{{ .BucketName }}"
  force_destroy = true

  tags = {
    DeploymentId = "{{ .DeploymentId }}"
    ComponentId  = "{{ .ComponentId }}"
    Project = "{{ .DeploymentId }}"
  }
}

resource "aws_s3_bucket_public_access_block" "{{ .ComponentId }}_block" {
  bucket = aws_s3_bucket.{{ .ComponentId }}.id

  block_public_acls       = false
  ignore_public_acls      = false
  block_public_policy     = false   # ★ 이 줄이 중요!
  restrict_public_buckets = false
}

resource "aws_s3_bucket_ownership_controls" "{{ .ComponentId }}_ownership" {
  bucket = aws_s3_bucket.{{ .ComponentId }}.id

  rule {
    object_ownership = "ObjectWriter"
  }
}

resource "aws_s3_bucket_acl" "{{ .ComponentId }}_acl" {
  bucket = aws_s3_bucket.{{ .ComponentId }}.id
  acl    = "public-read"
  depends_on = [
    aws_s3_bucket_public_access_block.{{ .ComponentId }}_block,
    aws_s3_bucket_ownership_controls.{{ .ComponentId }}_ownership
  ]
}

resource "aws_s3_bucket_policy" "{{ .ComponentId }}_policy" {
  bucket = aws_s3_bucket.{{ .ComponentId }}.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid       = "PublicReadGetObject",
        Effect    = "Allow",
        Principal = "*",
        Action    = "s3:GetObject",
        Resource  = "${aws_s3_bucket.{{ .ComponentId }}.arn}/*"
      }
    ]
  })

  depends_on = [aws_s3_bucket_public_access_block.{{ .ComponentId }}_block]
}

resource "aws_s3_bucket_website_configuration" "{{ .ComponentId }}_website" {
  bucket = aws_s3_bucket.{{ .ComponentId }}.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

output "bucket_domain_name" {
  value = "http://${aws_s3_bucket.{{ .ComponentId }}.bucket}.s3-website.{{ .Region }}.amazonaws.com"
}
