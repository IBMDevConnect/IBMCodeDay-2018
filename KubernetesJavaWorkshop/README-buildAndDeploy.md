# Building, Developing and Deploying your Microservices (Optional)

### Prerequisites

Install the following dependencies to start working with the Java microservices.

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](https://maven.apache.org/install.html)
  * Need to add this to your PATH.

Verify you have the proper versions (Java 8 JDK (1.8.x) is required, but newer Maven versions should be fine):

```
$ java -version
java version "1.8.0_60"
Java(TM) SE Runtime Environment (build 1.8.0_60-b27)
Java HotSpot(TM) 64-Bit Server VM (build 25.60-b23, mixed mode)

$ mvn --version
Apache Maven 3.5.0 (ff8f5e7444045639af65f6095c62210b5713f426; 2017-04-03T14:39:06-05:00)
Maven home: /Users/svennam/Downloads/apache-maven-3.5.0
Java version: 1.8.0_60, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.12.1", arch: "x86_64", family: "mac"
```

## Understanding the project layout

For this, refer to the git repository you cloned from the code pattern.

Here you should see a number of directories. Primarily, the following folders contain all the microservices that make up the application:

```
schedule-app
session-app
speaker-app
vote-app
web-app
```

Each of these repos represents a MicroProfile Java microservice. Notably, you'll see that each of these repos has a server.xml file - this is a config file that tells the Liberty runtime which features to enable for the corresponding microservice. You'll note that each has the following `server.xml` entry:

```
<feature>microProfile-1.0</feature>
```

This feature contains a set of capabilities (and other _features_) that has been decided by the MicroProfile community to serve the common requirements for microservice development.

## Developing Java Microservices

It's fairly straightforward to make changes to the individual microservices. Within each directory is a Maven based project structure which can be imported into Eclipse or your favorite IDE. To make updates to the microservice, simply make the code changes and run `mvn package` to rebuild the application. This creates a newly compiled and packaged `.war` file in the `target` directory.

You may want to run your cluster locally when in development mode. To do so, you would use a tool called [Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/). However, in this set of exercises we'll be deploying to the dedicated Kubernetes cluster on IBM's Cloud - you should have created this as part of the [prerequisites](#Prerequisites).

## Build your microservices
The microservices have already been cloned. However, you should run through all the folders to build the dependencies and ensure they passing tests. You'll be running build commands with Maven - a software project management tool used to manage the dependencies and project structure of your microservices. Run the following commands:

```
cd schedule-app
mvn clean package

cd ../session-app
mvn clean package

cd ../speaker-app
mvn clean package

cd ../vote-app
mvn clean package

cd ../web-app
mvn clean package
```

At each step of the way, you should have seen a message indicating `BUILD SUCCESS`. Each build creates its own `.war` file in the `target` folder. This `.war` file is important - it is executed by the Java EE server in each Docker container.

## Build and push each of your microservices to DockerHub

### Prerequisites

[Create an account on DockerHub](https://hub.docker.com/). This gives you a namespace on their registry to push your Docker images. This namespace usually matches your Docker ID.

### Publish your Docker images

In this step, you'll build and push the Docker images representing each of the microservices as well as an Nginx loadbalancer. This is possible through the `Dockerfile`s that are present in each of the microservice directories. The Dockerfile for the NGINX layer is located at `nginx/Dockerfile`.

Dockerfiles are descriptor files that tell Docker the various dependencies needed to package an application along with its source-code and dependencies into a container. Let's quickly walkthrough the Dockerfile for `session-app`:

```
# The first line tells Docker to pull a "base" image that starts our container with
# a set dependencies like a basic operation system, Java, Liberty Microprofile, etc
FROM websphere-liberty:microProfile

# Executes a script that comes with the base image to install a feature to our server
RUN installUtility install  --acceptLicense logstashCollector-1.0

# Copies over our Liberty server config file into the container
COPY server.xml /config/server.xml

# Copies the locally built "war" application into the proper directory within the container
COPY target/microservice-session-1.0.0-SNAPSHOT.war /config/apps/session.war
```

With these four simple instructions, Docker creates an image for executing each of your microservices. There is a similar `Dockerfile` for your load-balancer which starts with the `nginx` base image, instead of `websphere-liberty:microProfile`.

## Build and Push Docker Images to Docker Hub
When running the following steps, make sure to replace `<docker_namespace>` with your own Docker ID. In my case, it's `svennam92`. Yours will be your own unique ID. Be sure to login first using `docker login`.

```
docker login

docker build -t <docker_namespace>/microservice-webapp web-app
docker push <docker_namespace>/microservice-webapp

docker build -t <docker_namespace>/microservice-vote-cloudant vote-app
docker push <docker_namespace>/microservice-vote-cloudant

docker build -t <docker_namespace>/microservice-schedule schedule-app
docker push <docker_namespace>/microservice-schedule

docker build -t <docker_namespace>/microservice-speaker speaker-app
docker push <docker_namespace>/microservice-speaker

docker build -t <docker_namespace>/microservice-session session-app
docker push <docker_namespace>/microservice-session

docker build -t <docker_namespace>/nginx-server nginx
docker push <docker_namespace>/nginx-server
```

Now the images are available in a public registry. You need them in an accessible location because when you deploy your microservices to the Kubernetes cluster, you won't upload them directly from your laptop. Instead, you'll simply upload a manifest file to the cluster. This manifest tells your cluster to fetch and download the registry images directly from the registry - this saves time and allows your cluster to automatically keep our microservices up-to-date.

## Customize deploy manifests

Change the image name given in the respective deployment YAML files for all the projects in the `manifests` directory (in the top-level directory in the repo) with the newly built/pushed image names. You'll have to do once for each of the following files:

```
deploy-schedule.yaml
deploy-session.yaml
deploy-speaker.yaml
deploy-vote.yaml
deploy-webapp.yaml
```

For example, in the `manifests/deploy-speaker.yaml` file, change the following except with your own Docker Hub namespace:

```
image: journeycode/microservice-speaker
to
image: svennam92/microservice-speaker
```

## Next steps

Jump back to the [main instructions](README.md#step-2-deploy-your-microservices-to-your-kubernetes-cluster) and deploy your microservices. As long as you've properly updated your manifest files, the instructions will deploy your custom microservices on your DockerHub rather than the prebuilt ones.