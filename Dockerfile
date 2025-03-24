FROM openjdk:17-jdk
EXPOSE 8080
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=50"