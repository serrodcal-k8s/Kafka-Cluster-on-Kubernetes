FROM openjdk:8-jdk-alpine

COPY target/kafka-consumer-0.1.0-SNAPSHOT-allinone.jar /opt/kafka-consumer.jar

RUN addgroup -S appgroup &&\
    adduser -S -G appgroup appuser &&\
    chmod -R 777 /opt/ &&\
    chown -R appuser:appgroup /opt/

USER appuser

WORKDIR /opt/

ENTRYPOINT java -jar kafka-consumer.jar
