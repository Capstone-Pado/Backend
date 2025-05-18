provider "aws" {
  region     = "{{ .Region }}"
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

  user_data = <<-EOF
              #!/bin/bash
              dd if=/dev/zero of=/swapfile bs=128M count=16
              chmod 600 /swapfile
              mkswap /swapfile
              swapon /swapfile
              echo "/swapfile none swap sw 0 0" >> /etc/fstab
              EOF
  tags = {
    Name = "{{ .InstanceName }}"
  }
  vpc_security_group_ids = [aws_security_group.{{ .ComponentId }}-sg.id]
}

resource "aws_security_group" "{{ .ComponentId }}-sg" {
  name = "{{ .ComponentId }}-sg"

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