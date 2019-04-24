# Simple stream transformation

This project uses [Kafka on K8s Cluster by Helm](https://github.com/serrodcal/Kafka-Cluster-on-Kubernetes),
go there to get K8s.

## Building

```bash
~$ mvn package
```

### Docker image

```bash
~$ docker build -t serrodcal/kafka-stream-transformer:0.1.0 .
```

## Setup

Get Zookeeper IP and copy the IP:

````bash
~$ kubectl get pod my-kafka-zookeeper-0 --template={{.status.podIP}}
````

Get into a Kafka broker:

````bash
~$ kubectl exec -ti my-kafka-0 -- bash
````

Create two topic for transformation:

````bash
root@my-kafka-0:/# ./usr/bin/kafka-topics --create --zookeeper 10.1.6.178:2181 --topic topic-producer --replication-factor 1 --partitions 1
   Created topic "topic-producer".
root@my-kafka-0:/# ./usr/bin/kafka-topics --create --zookeeper 10.1.6.178:2181 --topic topic-consumer --replication-factor 1 --partitions 1
   Created topic "topic-consumer".
````

## Deploying

First, deploy the producer:

````bash
~$ kubectl create -f producer-transformer.yml
````

Second, deploy the stream transformer:

````bash
~$ kubectl create -f stream-transformer.yml
````

Finally, deploy the consumer:

````bash
~$ kubectl create -f stream-transformer.yml
````

### Checking transformation

You should see numbers until `200000`:

````bash
~$ kubectl logs transformer
...
ConsumerRecord(topic = topic-consumer, partition = 0, leaderEpoch = 0, offset = 99997, CreateTime = 1556091665275, serialized key size = -1, serialized value size = 6, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = 199996)
ConsumerRecord(topic = topic-consumer, partition = 0, leaderEpoch = 0, offset = 99998, CreateTime = 1556091665275, serialized key size = -1, serialized value size = 6, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = 199998)
ConsumerRecord(topic = topic-consumer, partition = 0, leaderEpoch = 0, offset = 99999, CreateTime = 1556091665275, serialized key size = -1, serialized value size = 6, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = 200000)
````


