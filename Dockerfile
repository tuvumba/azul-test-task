FROM openjdk:21-slim

WORKDIR /app
COPY . /app
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "build/libs/azul-test-task-0.0.1-SNAPSHOT.jar"]
