# REST Producer using Akka Stream and Akka Http

This project uses [Kafka on K8s Cluster by Helm](https://github.com/serrodcal/Kafka-Cluster-on-Kubernetes),
go there to get K8s.

## Building application

```bash
~$ mvn package
```

## Building Docker image

```bash
~$ docker build -t serrodcal/kafka-rest-producer:0.1.0 .
```

## Deploying on Kubernetes

```bash
~$ kubectl create -f rest-producer.yml
```

## Testing

```bash
~$ kubectl logs -f rest-producer
```

[Topic config](https://kafka.apache.org/documentation.html#topicconfigs)

Open new console tab, and run as given below:

```bash
~$ kubectl port-forward producer 8080:8080
```

Then, send request as given below:

```bash
~$ curl -i -X POST -d 'foo' 'http://localhost:8080/produce'
```

Use simple [consumer](https://github.com/serrodcal/Kafka-Cluster-on-Kubernetes/tree/master/consumer)
provided in this project to watch messages sent from topic.
