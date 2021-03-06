FROM openjdk:12.0.2-jdk-oracle

RUN         mkdir /app
WORKDIR     /app
ADD         .mvn .mvn
ADD         mvnw .

RUN         ./mvnw -v

ADD         oms.yml .
ADD         pom.xml .
ADD         src src

RUN         ls  /app/
RUN         ./mvnw package

ENTRYPOINT  java -jar /app/target/rabbitmq-1.0-SNAPSHOT.jar