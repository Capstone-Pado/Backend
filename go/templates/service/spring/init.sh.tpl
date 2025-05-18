#!/bin/bash

set -e
echo "[INFO] Spring App Init Script 시작"



# 1. Docker & Git 설치
sudo apt update -y
sudo apt install -y docker.io git 

# Java 설치
sudo apt install -y openjdk-{{ .JDKVersion }}-jdk

# 2. 작업 디렉토리 설정
cd /home/ubuntu

# 3. Git 클론
echo "[INFO] Git 리포지토리 클론: {{ .GitRepo }}"
git clone {{ .GitRepo }} {{ .ComponentId }}
cd {{ .ComponentId }}

# 4. .env 파일 생성
echo "[INFO] .env 파일 생성"
cat <<EOF > .env
{{- range $key, $value := .Env }}
{{ $key }}={{ $value }}
{{- end }}
EOF

# 4. Gradle/Maven Wrapper 파일 생성
{{- if eq .BuildTool "gradle" }}
./gradlew build --no-daemon
{{- else if eq .BuildTool "maven" }}
./mvnw package
{{- else }}
./gradlew build --no-daemon
{{- end }}

# 5. Dockerfile 생성
echo "[INFO] Dockerfile 생성"
JAR_NAME=$(find build/libs -name '*.jar' | grep -v plain | tail -n 1)
cat <<DOCKER > Dockerfile
FROM openjdk:{{ .JDKVersion }}-jdk-slim
WORKDIR /app
COPY . .

{{- if eq .BuildTool "gradle" }}
CMD ["java", "-jar", "$JAR_NAME"]
{{- else if eq .BuildTool "maven" }}
CMD ["java", "-jar", "$JAR_NAME"]
{{- else }}
# 기본은 gradle
CMD ["java", "-jar", "$JAR_NAME"]
{{- end }}

EXPOSE {{ .DockerPort }}
DOCKER

# 6. Docker 빌드 및 실행
sudo docker build -t {{ .ComponentId }} .
sudo docker run -d -p {{ .DockerPort }}:{{ .DockerPort }} \
  --env-file .env \
  --name {{ .ComponentId }} \
  {{ .ComponentId }}

echo "[SUCCESS] Spring 서비스 {{ .ComponentId }} 배포 완료!"

# 7. nginx.conf 생성
echo "[INFO] Nginx 설정 생성"
cat <<NGINX > nginx.conf
events {}
http {
  server {
    listen 80;

    location / {
      proxy_pass http://localhost:{{ .DockerPort }};
      proxy_set_header Host \$host;
      proxy_set_header X-Real-IP \$remote_addr;
      proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto \$scheme;

      # CORS 설정
      add_header 'Access-Control-Allow-Origin' \$http_origin always;
      add_header 'Access-Control-Allow-Credentials' 'true' always;
      add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
      add_header 'Access-Control-Allow-Headers' 'Origin, Content-Type, Accept, Authorization' always;

      if (\$request_method = OPTIONS) {
        return 204;
      }
    }
  }
}
NGINX

# 8. Nginx 컨테이너 실행
sudo docker run -d --name {{ .ComponentId }}-nginx \
  -v $(pwd)/nginx.conf:/etc/nginx/nginx.conf:ro \
  -p {{ .NginxPort }}:80 \
  --network host \
  nginx:stable

echo "[SUCCESS] Spring 서비스 {{ .ComponentId }} + Nginx 배포 완료!"