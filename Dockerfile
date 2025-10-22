# ===============================
#   Stage 1 - Build Application
# ===============================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven files first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project and build
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
#   Stage 2 - Run Application
# ===============================
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/task-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables (optional)
ENV SPRING_PROFILES_ACTIVE=prod

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
