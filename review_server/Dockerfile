# 1) Build stage (JDK 21)
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

COPY . .

# ✅ COPY 이후 권한이 덮일 수 있어서 한 번 더 보강
RUN chmod +x gradlew && ./gradlew clean build -x test --no-daemon

# 2) Runtime stage (JRE 21)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=builder /app/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
