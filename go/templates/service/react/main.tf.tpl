provider "aws" {
  region     = "{{ .Region }}"
  access_key = "{{ .AWSAccessKey }}"
  secret_key = "{{ .AWSSecretKey }}"
}

resource "aws_key_pair" "generated" {
  key_name   = "tmp"
  public_key = <<EOT
{{ .PublicKey }}
EOT
}

resource "aws_instance" "app" {
  ami           = "{{ .AMI }}"
  instance_type = "t3.micro"
  key_name      = "tmp"
  tags = {
    Name = "Builder"
    Project = "{{ .DeploymentId }}"
  }
  vpc_security_group_ids = [aws_security_group.app_sg.id]

}

resource "aws_security_group" "app_sg" {
  name = "app_sg"

  ingress {
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Project = "{{ .DeploymentId }}"
  }
}

output "instance_ip" {
  value = aws_instance.app.public_ip
}