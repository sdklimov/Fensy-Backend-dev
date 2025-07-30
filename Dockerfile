FROM openjdk:26-oraclelinux8
WORKDIR /app
COPY build/libs/fensy-backend-dev.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
