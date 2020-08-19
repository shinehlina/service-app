FROM adoptopenjdk/openjdk11:ubi
ADD target/test-service-0.1.2-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]