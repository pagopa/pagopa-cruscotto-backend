# Step 1: Use Maven to build the project
FROM maven:3.9.5-amazoncorretto-21-al2023 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Use Amazon Corretto JDK to run the application
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot application runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]