    # ---------- Build stage ----------
    FROM maven:3.9.9-eclipse-temurin-17 AS build

    WORKDIR /app
    
    # Copy pom first to leverage Docker cache
    COPY pom.xml .
    RUN mvn -B -q dependency:go-offline
    
    # Copy source and build
    COPY src ./src
    RUN mvn -B -q clean package -DskipTests
    
    
    # ---------- Runtime stage ----------
    FROM eclipse-temurin:17-jre
    
    WORKDIR /app
    
    # Copy only the built jar
    COPY --from=build /app/target/*jar app.jar
    
    # Spring Boot port
    EXPOSE 8081
    
    # JVM tuning for containers
    ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
    
    ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
    