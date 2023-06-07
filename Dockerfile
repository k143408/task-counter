FROM openjdk:17-jdk-slim-buster
COPY target/app.jar /opt/service/
WORKDIR /opt/service
ENTRYPOINT java -jar app.jar
