FROM openjdk:8-jdk-alpine

COPY target/kafka-consumer-0.1.0-SNAPSHOT-allinone.jar /opt/kafka-consumer.jar
COPY src/main/resources/application.conf /opt/application.conf

RUN addgroup -S appgroup &&\
    adduser -S -G appgroup appuser &&\
    chmod -R 777 /opt/ &&\
    chown -R appuser:appgroup /opt/

USER appuser

WORKDIR /opt/

ENTRYPOINT java -Dconfig.file=/opt/application.conf -jar kafka-consumer.jar
