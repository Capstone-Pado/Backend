#!/bin/bash

set -e
echo "[INFO] MySQL Init Script 시작"

# 1. Docker 설치
sudo apt update -y
sudo apt install -y docker.io

# 2. 작업 디렉토리로 이동
mkdir -p /home/ubuntu/{{ .ComponentId }}
sudo chown -R ubuntu:ubuntu /home/ubuntu/{{ .ComponentId }}
sudo chmod -R 755 /home/ubuntu/{{ .ComponentId }}
cd /home/ubuntu/{{ .ComponentId }}
mkdir -p mysql-data

# 3. .env 파일 생성
echo "[INFO] .env 파일 생성"
cat <<EOF > .env
MYSQL_ROOT_PASSWORD={{ .DBConfig.MySQLRootPassword }}
MYSQL_DATABASE={{ .DBConfig.MySQLDatabase }}
MYSQL_USER={{ .DBConfig.MySQLUser }}
MYSQL_PASSWORD={{ .DBConfig.MySQLPassword }}
EOF

# 4. Docker로 MySQL 실행
sudo docker run -d \
  --name {{ .ComponentId }} \
  --env-file .env \
  -v $(pwd)/mysql-data:/var/lib/mysql \
  -p {{ .DBConfig.Port }}:3306 \
  mysql:8.0

echo "[SUCCESS] MySQL 서비스 {{ .ComponentId }} 배포 완료!"
