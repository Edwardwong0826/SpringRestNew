FROM adoptopenjdk/openjdk11:latest
EXPOSE 8082
ADD SpringRest-0.0.1-SNAPSHOT.jar SpringRest-0.0.1.jar
ENTRYPOINT ["java","-jar","/SpringRest-0.0.1.jar"]