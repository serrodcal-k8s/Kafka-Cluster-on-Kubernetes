# Simple Kafka Producer using Alpakka

This project uses [Kafka on K8s Cluster by Helm](https://github.com/serrodcal/Kafka-Cluster-on-Kubernetes),
go there to get K8s.

## Building application

```bash
~$ mvn package
```

## Building Docker image

```bash
~$ docker build -t serrodcal/kafka-producer:0.1.0 .
```

## Deploying on Kubernetes

```bash
~$ kubectl create -f producer.yml
```

## Testing

```bash
~$ kubectl logs -f producer
```
