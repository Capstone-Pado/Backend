provider "aws" {
  region     = "ap-northeast-2"
  access_key = "{{ .AWSAccessKey }}"
  secret_key = "{{ .AWSSecretKey }}"
}

resource "aws_key_pair" "generated" {
  key_name   = "{{ .KeyName }}"
  public_key = <<EOT
{{ .PublicKey }}
EOT
}

resource "aws_instance" "app" {
  ami           = "{{ .AMI }}"
  instance_type = "{{ .InstanceType }}"
  key_name      = "{{ .KeyName }}"
  tags = {
    Name = "{{ .InstanceName }}"
  }
  vpc_security_group_ids = [aws_security_group.app_sg.id]
}

resource "aws_security_group" "app_sg" {
  name = "app_sg"

  dynamic "ingress" {
    for_each = toset([
{{- range $i, $p := .OpenPorts }}
  {{- if $i}}, {{ end }}{{ $p }}
{{- end }}
])
    content {
      from_port   = ingress.value
      to_port     = ingress.value
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

output "instance_ip" {
  value = aws_instance.app.public_ip
}