FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar
ENV SERVER_PORT=8080
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
