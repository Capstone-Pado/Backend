#!/bin/bash
set -e

export AWS_ACCESS_KEY_ID="{{ .S3.AWSAccessKey }}"
export AWS_SECRET_ACCESS_KEY="{{ .S3.AWSSecretKey }}"

# 1. Node 설치 (Ubuntu)
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get update -y
sudo apt-get install -y nodejs git awscli

# 2. Git clone
git clone {{ .Service.GitRepo }} app
cd app

# 3. Build
npm install
npm run build

# 4. S3 업로드
aws s3 sync dist/ s3://{{ .S3.BucketName }} --region {{ .S3.Region }}
