BUILD_POSITION=$1
MODE=$2:-auth

# docker container stop hf-backend-app || true
# docker container rm hf-backend-app || true

docker-compose down

if [ "$BUILD_POSITION" = "no-container" ]; then
  ./gradlew clean build -x test
fi

docker build -t hf/backend .

if [ "$MODE" = "no-auth" ]; then
  docker-compose -f docker-compose.yml --profile blue-noauth up -d --build
else
  docker-compose -f docker-compose.yml --profile blue up -d --build
fi