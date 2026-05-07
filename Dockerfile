FROM gradle:8.7-jdk21-alpine AS builder

WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]