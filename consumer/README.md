# Simple Kafka Consumer using Alpakka

This project uses [Kafka on K8s Cluster by Helm](https://github.com/serrodcal/Kafka-Cluster-on-Kubernetes),
go there to get K8s.

## Building application

```bash
~$ mvn package
```

## Building Docker image

```bash
~$ docker build -t serrodcal/kafka-consumer:0.1.0 .
```

## Deploying on Kubernetes

```bash
~$ kubectl create -f consumer.yml
```

## Testing

```bash
~$ kubectl logs -f consumer
```

### Creating consumer group

A consumer group ID is configurable by environment variable. This mechanism allows us
to create several consumers who has the same consumer group ID.

By default, If you create several consumer using same `consumer.yml` file, all
consumers are in same consumer group, in this case, the name is `group1`.

After your Kafka cluster is up and running (in this case, we use 3 brokers):

1. Start up a producer:

```bash
~$ kubectl create -f producer/producer.yml
```

2. Start up the consumer group:

```bash
~$ kubectl create -f consumer/consumer-group.yml
```
