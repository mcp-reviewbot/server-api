#!/usr/bin/env bash

set -euo pipefail

APP_NAME="${APP_NAME:-review_server}"
IMAGE="${IMAGE:-choihyunsoo/review_server:latest}"

PORT="${PORT:-8080}"

docker pull "$IMAGE"

# 중복 컨테이너 삭제
if docker ps -a --format '{{.Names}}' | grep -q "^${APP_NAME}\$"; then
  docker stop "$APP_NAME" || true
  docker rm "$APP_NAME" || true
fi

# 컨테이너 실행
ENV_FILE="/opt/myapp/.env"

docker run -d \
  --name "$APP_NAME" \
  -p "${PORT}:8080" \
  --restart always \
  --env-file "$ENV_FILE" \
  -e SPRING_PROFILES_ACTIVE=deploy \
  "$IMAGE"


sleep 2

docker ps --filter "name=${APP_NAME}"
docker logs --tail=50 "$APP_NAME" || true
docker image prune -f || true
