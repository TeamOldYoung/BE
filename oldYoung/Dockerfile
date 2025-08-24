#빌드
FROM gradle:8.7.0-jdk17 AS builder
WORKDIR /app

# Gradle wrapper, build 파일 먼저 복사 → 캐시 최적화
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# 의존성 캐싱
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 및 빌드
COPY . .
RUN ./gradlew clean bootJar --no-daemon

# 실행
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# builder 단계에서 빌드된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# JVM 메모리 설정 (EC2 t2.micro 고려해 최소화)
ENTRYPOINT ["java","-Xmx512m","-Xms256m","-jar","app.jar"]

# Spring Boot 기본 포트
EXPOSE 8080