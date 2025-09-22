# Этап загрузки последней версии tonlib
FROM alpine:latest as downloader
RUN apk add --no-cache curl jq
WORKDIR /tmp
RUN LATEST_RELEASE=$(curl -s https://api.github.com/repos/ton-blockchain/ton/releases/latest | jq -r '.tag_name') && \
    curl -L -o tonlibjson-linux-x86_64.so "https://github.com/ton-blockchain/ton/releases/download/${LATEST_RELEASE}/tonlibjson-linux-x86_64.so"

# Основной этап с приложением
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY build/libs/*.jar app.jar
COPY --from=downloader /tmp/tonlibjson-linux-x86_64.so /app/tonlibjson-linux-x86_64.so
RUN chmod +x /app/tonlibjson-linux-x86_64.so
ENTRYPOINT ["java", "-jar", "app.jar"]