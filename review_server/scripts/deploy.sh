#!/usr/bin/env bash
set -euo pipefail

APP_NAME="${APP_NAME:-review_server}"
IMAGE="${IMAGE:-choihyunsoo/review_server:latest}"
PORT="${PORT:-8080}"

# (권장) 민감정보는 서버에 .env 파일로 두고 컨테이너에 주입
ENV_FILE="${ENV_FILE:-/opt/myapp/.env}"

echo "[1/6] Pull image: $IMAGE"
docker pull "$IMAGE"

echo "[2/6] Stop old container if exists"
if docker ps -a --format '{{.Names}}' | grep -q "^${APP_NAME}\$"; then
  docker stop "$APP_NAME" || true
  docker rm "$APP_NAME" || true
fi

echo "[3/6] Run new container"
# .env가 없으면 ENV_FILE 옵션을 제거하거나, 파일을 생성해두면 됨
RUN_ENV_ARGS=()
if [ -f "$ENV_FILE" ]; then
  RUN_ENV_ARGS+=(--env-file "$ENV_FILE")
fi

docker run -d \
  --name "$APP_NAME" \
  -p "${PORT}:8080" \
  --restart always \
  "${RUN_ENV_ARGS[@]}" \
  "$IMAGE"

echo "[4/6] Health check (simple)"
sleep 2
docker ps --filter "name=${APP_NAME}"

echo "[5/6] Tail logs (last 50 lines)"
docker logs --tail=50 "$APP_NAME" || true

echo "[6/6] Cleanup dangling images"
docker image prune -f || true

echo "✅ Deploy done."
