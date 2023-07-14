FROM openjdk:17-jdk-alpine3.14
EXPOSE 8081
# ADD [source] [destination] means copy source path file to destination with image
ADD target/SpringRestNew-0.0.1-SNAPSHOT.jar SpringRestNew-0.0.1.jar
ENTRYPOINT ["java","-jar","/SpringRestNew-0.0.1.jar"]