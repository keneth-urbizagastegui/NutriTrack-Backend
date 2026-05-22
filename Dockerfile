# === Stage 1: build con Maven ===
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
# Cache de dependencias (copiar primero el pom)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# === Stage 2: runtime ligero ===
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd -r -u 1001 -g root spring
USER 1001

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENV SPRING_PROFILES_ACTIVE=prod

HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
