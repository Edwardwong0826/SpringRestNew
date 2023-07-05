FROM adoptopenjdk/openjdk11:jre-11.0.19_7-alpine
EXPOSE 8081
ADD SpringRestNew-0.0.1-SNAPSHOT.jar SpringRestNew-0.0.1.jar
ENTRYPOINT ["java","-jar","/SpringRestNew-0.0.1.jar"]