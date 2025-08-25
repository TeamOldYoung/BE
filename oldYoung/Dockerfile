# Jenkins에서 빌드된 JAR을 사용하는 경량화된 Docker 이미지
FROM eclipse-temurin:17-jre-jammy

# curl 설치 및 보안 설정
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Jenkins에서 빌드된 jar 파일 복사
COPY build/libs/*.jar app.jar

# 파일 권한 설정
RUN chown appuser:appgroup /app/app.jar

# 보안을 위해 non-root 사용자로 실행
USER appuser

# JVM 최적화 설정
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Spring Boot Actuator health check 지원
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 실행 명령어
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Spring Boot 기본 포트
EXPOSE 8080