FROM adoptopenjdk/openjdk11:ubi
ADD target/test-service-0.1.1.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]