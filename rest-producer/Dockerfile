FROM openjdk:8-jdk-alpine

COPY target/kafka-rest-producer-0.1.0-SNAPSHOT-allinone.jar /opt/kafka-rest-producer.jar
COPY src/main/resources/application.conf /opt/application.conf

RUN addgroup -S appgroup &&\
    adduser -S -G appgroup appuser &&\
    chmod -R 777 /opt/ &&\
    chown -R appuser:appgroup /opt/

USER appuser

EXPOSE 8080

WORKDIR /opt/

ENTRYPOINT java -Dconfig.file=/opt/application.conf -jar kafka-rest-producer.jar
