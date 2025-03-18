# Stage 1: Build the application with Maven
FROM maven:latest AS builder
WORKDIR /app
COPY . /app
#RUN mvn clean install -x test
# this row can be deleted in develop cases
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

# Stage 2: Run the application with OpenJDK 21
FROM openjdk:21-slim
WORKDIR /app
COPY ./opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
COPY --from=builder /app/target/*.jar /app/lexiscan-app.jar
EXPOSE 8181
ENTRYPOINT ["java", "-jar", "/app/lexiscan-app.jar"]