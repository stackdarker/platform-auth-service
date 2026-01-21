# ---- build stage ----
    FROM maven:3.9-eclipse-temurin-17 AS build
    WORKDIR /app
    
    COPY pom.xml .
    COPY src ./src
    
    # Build the Spring Boot fat jar (creates *.jar and *.jar.original)
    RUN mvn -DskipTests clean package
    
    # Copy ONLY the fat jar (exclude *.jar.original)
    RUN set -eux; \
        JAR="$(ls -1 target/*.jar | grep -v '\.jar\.original$' | head -n 1)"; \
        cp "$JAR" /app/app.jar; \
        echo "Using jar: $JAR"; \
        jar tf /app/app.jar | head -n 5
    
    # ---- runtime stage ----
    FROM eclipse-temurin:17-jre
    WORKDIR /app
    
    COPY --from=build /app/app.jar /app/app.jar
    
    EXPOSE 8081
    ENTRYPOINT ["java","-jar","/app/app.jar"]
    