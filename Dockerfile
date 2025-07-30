FROM debian:bullseye-slim

RUN apt update && apt install -y \
    openjdk-21-jdk \
    curl \
    libstdc++6 \
    libgcc-s1 \
    && apt clean
#FROM openjdk:26-jdk-oraclelinux8
WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
