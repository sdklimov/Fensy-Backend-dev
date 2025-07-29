FROM openjdk:21

RUN apt-get update && \
    apt-get install -y curl ca-certificates && \
    rm -rf /var/lib/apt/lists/* \

WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
