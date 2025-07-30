FROM alpine/java:22-jdk
WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
