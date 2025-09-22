FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV APP_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE ${APP_PORT}
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
