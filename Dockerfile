FROM debian:bullseye-slim
RUN apt update && apt install -y \
    openjdk-17-jdk \
    curl \
    libstdc++6 \
    libgcc-s1 \
    && apt clean
WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]