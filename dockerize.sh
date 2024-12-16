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
  docker run --name hf-backend-app -p 8080:8080 -dit --rm -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_REGION=$AWS_REGION -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -e AWS_SQS_ALARM_QUEUE_URL=$AWS_SQS_ALARM_QUEUE_URL -e AWS_SQS_DB_QUEUE_URL=$AWS_SQS_DB_QUEUE_URL --network=hf-net hf-backend
else
  docker run --name hf-backend-app -p 8080:8080 -dit --rm -e SPRING_PROFILES_ACTIVE=local-dev,secret,constants,priv -e AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -e AWS_REGION=$AWS_REGION -e AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY -e AWS_SQS_ALARM_QUEUE_URL=$AWS_SQS_ALARM_QUEUE_URL -e AWS_SQS_DB_QUEUE_URL=$AWS_SQS_DB_QUEUE_URL --network=hf-net hf-backend
fi