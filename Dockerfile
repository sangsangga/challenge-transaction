# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s CMD curl -fsS http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]


