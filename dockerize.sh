AWS_ACCESS_KEY_ID=$1
AWS_REGION=$2
AWS_SECRET_ACCESS_KEY=$3
AWS_SQS_ALARM_QUEUE_URL=$4
AWS_SQS_DB_QUEUE_URL=$5

MODE=$6:-auth

docker container stop hf-backend-app || true
docker container rm hf-backend-app || true

./gradlew clean build -x test

docker build -t hf-backend .

if [ "$MODE" = "no-auth" ]; then
  docker-compose -f docker-compose.yml up -d --build
else
  docker-compose -f docker-compose.yml up -d --build
fi