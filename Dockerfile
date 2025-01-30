FROM openjdk:17-jdk
EXPOSE 8080
COPY build/libs/*SHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]