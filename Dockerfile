FROM openjdk:21
VOLUME /tmp
EXPOSE 8081
COPY build/libs/azul-test-task-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
