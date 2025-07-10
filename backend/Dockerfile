# Use a slim but cross-platform compatible image
FROM bellsoft/liberica-openjdk-alpine:17
COPY target/TechInterviewWeb-Backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
