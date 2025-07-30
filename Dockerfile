FROM ubuntu:rolling

# Установка OpenJDK 21 и зависимостей с очисткой кеша
RUN apt update && apt install -y --no-install-recommends \
    openjdk-21-jdk \
    curl \
    libstdc++6 \
    libgcc-s1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]