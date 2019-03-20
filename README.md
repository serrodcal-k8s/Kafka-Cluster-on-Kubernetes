# Kafka-Cluster-on-Kubernetes

This repo consists of information about how to install a Kafka cluster on
Kubernetes (specifically, [docker-for-desktop](https://medium.com/containers-101/local-kubernetes-for-mac-minikube-vs-docker-desktop-f2789b3cad3a)
version) and several scenarios for a full testing of Kafka's capabilities.

## Getting Started

### Prerequisites

Install [Docker](https://www.docker.com/), [Helm](https://helm.sh/) and [Kafkacat](https://github.com/edenhill/kafkacat).

**Configure the memory of Docker** by default Docker limits itself to 4G of memory,
for this project this is often too little, you can increase it to 8G by going
to _Docker >> Preferences >> Advanced >> change the Memory_.

**Enable Kubernetes** In Docker go to the preferences and to the tab Kubernetes
and check Enable Kubernetes This will install and start Kubernetes. Click on the Docker icon
to view the status of Kubernets.

**NOTE**: if you are already connected to an environment make sure to switch back to your local environment before you run
below scripts running `kubectl config use-context docker-for-desktop`

### Installing

Add repo to install the chart:

```bash
~$ helm repo add incubator http://storage.googleapis.com/kubernetes-charts-incubator
```

Before you install the chart with the release name `my-kafka`, initialize Helm:

```bash
~$ helm init --wait
$HELM_HOME has been configured at /Users/<your_user>/.helm.

Tiller (the Helm server-side component) has been installed into your Kubernetes Cluster.

Please note: by default, Tiller is deployed with an insecure 'allow unauthenticated users' policy.
To prevent this, run `helm init` with the --tiller-tls-verify flag.
For more information on securing your installation see: https://docs.helm.sh/using_helm/#securing-your-helm-installation
Happy Helming!
```

You should see `Happy Helming!`. Helm has two parts: Helm (client) and Tiller (server),
which should be now running on Kubernetes as given below:

```bash
~$ kubectl -n kube-system get pods
NAME                                         READY   STATUS    RESTARTS   AGE
etcd-docker-for-desktop                      1/1     Running   0          60d
kube-apiserver-docker-for-desktop            1/1     Running   0          60d
kube-controller-manager-docker-for-desktop   1/1     Running   0          60d
kube-dns-86f4d74b45-kls2d                    3/3     Running   0          60d
kube-proxy-zpcbr                             1/1     Running   0          60d
kube-scheduler-docker-for-desktop            1/1     Running   0          60d
kubernetes-dashboard-7b9c7bc8c9-pc5wm        1/1     Running   1          60d
tiller-deploy-9cb565677-fnm2b                1/1     Running   0          19s
```

`tiller-deploy-9cb565677-fnm2b` is now running.

Now, we are able to install Kafka:

```bash
~$ helm install --name my-kafka incubator/kafka
NAME:   my-kafka
LAST DEPLOYED: Mon Mar 18 18:45:23 2019
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1/Pod(related)
NAME                  READY  STATUS             RESTARTS  AGE
my-kafka-0            0/1    Pending            0         0s
my-kafka-zookeeper-0  0/1    ContainerCreating  0         0s

==> v1/Service
NAME                         TYPE       CLUSTER-IP      EXTERNAL-IP  PORT(S)                     AGE
my-kafka                     ClusterIP  10.97.248.107   <none>       9092/TCP                    0s
my-kafka-headless            ClusterIP  None            <none>       9092/TCP                    0s
my-kafka-zookeeper           ClusterIP  10.104.105.157  <none>       2181/TCP                    0s
my-kafka-zookeeper-headless  ClusterIP  None            <none>       2181/TCP,3888/TCP,2888/TCP  0s

==> v1beta1/PodDisruptionBudget
NAME                MIN AVAILABLE  MAX UNAVAILABLE  ALLOWED DISRUPTIONS  AGE
my-kafka-zookeeper  N/A            1                0                    0s

==> v1beta1/StatefulSet
NAME                READY  AGE
my-kafka            0/3    0s
my-kafka-zookeeper  0/3    0s
```

This provides us a mode to test Kafka. You can choose this or you can use Kafkacat as given below.

If using a dedicated namespace(recommended) then make sure the namespace exists with:

```bash
~$ kubectl create ns kafka
~$ helm install --name my-kafka --namespace kafka incubator/kafka
```

Now, you should have three Kafka replicas and three Zookeeper replicas which make up the cluster:

```bash
~$ kubectl get pods
NAME                   READY   STATUS    RESTARTS   AGE
my-kafka-0             1/1     Running   0          16m
my-kafka-1             1/1     Running   0          14m
my-kafka-2             1/1     Running   0          13m
my-kafka-zookeeper-0   1/1     Running   0          16m
my-kafka-zookeeper-1   1/1     Running   0          15m
my-kafka-zookeeper-2   1/1     Running   0          14m
```

### Listing

```bash
~$ helm ls
NAME    	REVISION	UPDATED                 	STATUS  	CHART        	APP VERSION	NAMESPACE
my-kafka	1       	Mon Mar 18 18:45:23 2019	DEPLOYED	kafka-0.13.11	5.0.1      	default  
```

### Scaling

```bash
helm status my-kafka   
LAST DEPLOYED: Tue Mar 19 17:04:55 2019
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1/Pod(related)
NAME                  READY  STATUS   RESTARTS  AGE
my-kafka-0            0/1    Running  1         82s
my-kafka-zookeeper-0  1/1    Running  0         82s
my-kafka-zookeeper-1  1/1    Running  0         51s
my-kafka-zookeeper-2  1/1    Running  0         28s

==> v1/Service
NAME                         TYPE       CLUSTER-IP      EXTERNAL-IP  PORT(S)                     AGE
my-kafka                     ClusterIP  10.107.14.94    <none>       9092/TCP                    82s
my-kafka-headless            ClusterIP  None            <none>       9092/TCP                    82s
my-kafka-zookeeper           ClusterIP  10.103.237.247  <none>       2181/TCP                    82s
my-kafka-zookeeper-headless  ClusterIP  None            <none>       2181/TCP,3888/TCP,2888/TCP  82s

==> v1beta1/PodDisruptionBudget
NAME                MIN AVAILABLE  MAX UNAVAILABLE  ALLOWED DISRUPTIONS  AGE
my-kafka-zookeeper  N/A            1                1                    82s

==> v1beta1/StatefulSet
NAME                READY  AGE
my-kafka            0/3    82s
my-kafka-zookeeper  3/3    82s
```

Take a look into `StatefulSet`:

```
==> v1beta1/StatefulSet
NAME                READY  AGE
my-kafka            0/3    82s
my-kafka-zookeeper  3/3    82s
```

For scaling, run following command:

```bash
~$ kubectl scale --replicas 1 StatefulSet/my-kafka
```

Change replicas number according you need.

```bash
~$ kubectl get pods
NAME                   READY   STATUS    RESTARTS   AGE
my-kafka-0             1/1     Running   1          6m
my-kafka-zookeeper-0   1/1     Running   0          6m
my-kafka-zookeeper-1   1/1     Running   0          5m
my-kafka-zookeeper-2   1/1     Running   0          5m
```

If you want to scale Zookeeper run the following command:

```bash
~$ kubectl scale --replicas 1 StatefulSet/my-kafka-zookeeper
```

The result is as given below:

```bash
~$ kubectl get pods                                         
NAME                   READY   STATUS    RESTARTS   AGE
my-kafka-0             1/1     Running   1          8m
my-kafka-zookeeper-0   1/1     Running   0          8m
```

Now, you can configure your test environment.

### Deleting

```bash
~$ helm delete my-kafka
```

If you get that service already exists, use `--purge` for deleting after re-install chart:

```bash
~$ helm del --purge my-kafka
```

## Running the tests

Create a test pod. Save following code as file (for instance, save as `testclient.yml`):

```bash
apiVersion: v1
kind: Pod
metadata:
  name: testclient
  namespace: default
spec:
  containers:
  - name: kafka
    image: confluentinc/cp-kafkacat
    imagePullPolicy: IfNotPresent
    command:
      - sh
      - -c
      - "exec tail -f /dev/null"
```

Create testclient pod:

```bash
~$ kubectl create -f testclient.yml
```

Get into testclient pod for testing:

```bash
~$ kubectl exec -ti testclient -- bash
```

Once you are in testclient pod, test as given below:

* List all Kafka topics:

```bash
~$ kafkacat -b my-kafka:9092 -L
```

* Producing messages inline from a script:

```bash
~$ kafkacat -b my-kafka:9092 -t test -K: -P <<EOF
1: FOO
2: BAR
EOF
```

Or, without keys:

```bash
~$ kafkacat -P -b my-kafka:9092 -t myTopic <<EOF
foo
bar
EOF
```

In addition, if you want to publish just a message:

```bash
~$ echo "foo" | kafkacat -P -b localhost:9092 -t myTopic
```

* Consuming messages from a topic

```bash
~$ kafkacat -b my-kafka:9092 -C -K: -f '\nKey (%K bytes): %k\t\nValue (%S bytes): %s\n\Partition: %p\tOffset: %o\n--\n' -t test

Key (1 bytes): 1
Value (4 bytes):  FOO
Partition: 0	Offset: 0
--

Key (1 bytes): 2
Value (4 bytes):  BAR
Partition: 0	Offset: 1
--
```

Or, without keys:

```bash
~$ kafkacat -C -b my-kafka:9092 -t myTopic
foo
bar
```

## Built with

* [Docker](https://www.docker.com/)
* [Kubernetes](https://kubernetes.io/)
* [Helm](https://helm.sh/)
* [Kafkacat](https://github.com/edenhill/kafkacat)
