# Use an official OpenJDK base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/video-describe-client-1.0-SNAPSHOT.jar /app/video-describe-client-1.0-SNAPSHOT.jar

# Expose the port the application runs on (optional)
EXPOSE 8081

# Run the Java application
CMD ["java", "-jar", "video-describe-client-1.0-SNAPSHOT.jar"]
