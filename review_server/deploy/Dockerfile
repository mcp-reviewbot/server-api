#build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY ../gradlew .
COPY ../gradle gradle
COPY ../build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

COPY .. .

RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon

#run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
