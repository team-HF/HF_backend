AWS_ACCESS_KEY_ID=$1
AWS_REGION=$2
AWS_SECRET_ACCESS_KEY=$3
AWS_SQS_ALARM_QUEUE_URL=$4
AWS_SQS_DB_QUEUE_URL=$5

BUILD_POSITION=$6
MODE=$7:-auth

docker container stop hf-backend-app || true
docker container rm hf-backend-app || true

if [ "$BUILD_POSITION" = "container" ]; then
  ./gradlew clean build -x test
fi

docker build -t hf-backend .

if [ "$MODE" = "no-auth" ]; then
  docker-compose -f docker-compose.yml up -d --build
else
  docker-compose -f docker-compose.yml up -d --build
fi