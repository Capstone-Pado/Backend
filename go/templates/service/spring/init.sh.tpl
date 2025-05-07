#!/bin/bash

set -e
echo "[INFO] Spring App Init Script 시작"

# 1. Docker & Git 설치
sudo apt update -y
sudo apt install -y docker.io git

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

# 5. Dockerfile 생성
echo "[INFO] Dockerfile 생성"
cat <<DOCKER > Dockerfile
FROM openjdk:{{ .JDKVersion }}-jdk-slim
WORKDIR /app
COPY . .

{{- if eq .BuildTool "gradle" }}
RUN ./gradlew build --no-daemon
CMD ["java", "-jar", "$(find build/libs -name '*.jar' | head -n 1)"]
{{- else if eq .BuildTool "maven" }}
RUN ./mvnw package
CMD ["java", "-jar", "$(find target -name '*.jar' | head -n 1)"]
{{- else }}
# 기본은 gradle
RUN ./gradlew build --no-daemon
CMD ["java", "-jar", "$(find build/libs -name '*.jar' | head -n 1)"]
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
