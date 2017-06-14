# ozwillo-ozenergy

OzEnergy - energy consumption monitoring for consumers, providers and territories alike

![Demo](https://github.com/ozwillo/ozwillo-ozenergy/blob/master/app-overview/oz-energy.gif)

## How to use it

### Through a docker image

If you have Docker installed (if not, you can find how to do so [here](https://docs.docker.com/engine/installation/)), and don't want to mess with your pre-existing dev environment, you may prefer this to the next section ("Or build and run it locally").

``` bash
git clone https://github.com/occiware/ozwillo-ozenergy.git
cd ozwillo-ozenergy
sudo docker build -t ozenergy .
sudo docker run -p 8080:8080 ozenergy
```

### Or build and run it locally

#### Prerequisites

Building requires [**Java 8**](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [**Maven 3**](https://maven.apache.org/download.cgi), [**Node 6**](https://nodejs.org/en/download/releases/) (tested with v6.2.0 and v6.3.0), [**Scala 2.10** or later](http://www.scala-lang.org/download/2.11.0.html) , [**sbt 0.13**](http://www.scala-sbt.org/0.13/docs/Setup.html) and [**Spark 1.6.1** (pre-built for Hadoop 2.6)](http://spark.apache.org/downloads.html).

Running requires also [**MongoDB 2.6**](https://docs.mongodb.com/v2.6/installation/).

In the end what you need to do is :

+ A priori, it is not necessary to install Scala separately, since sbt can install the most appropriate version itself. So go on the [**sbt 0.13**](http://www.scala-sbt.org/0.13/docs/Setup.html) link and follow the installation instructions.

+ Java, Maven and MongoDB are to be installed through command line

``` bash
sudo apt-get install default-jre default-jdk maven mongodb
```

+ After downloading the [**Spark 1.6.1** (pre-built for Hadoop 2.6)](http://spark.apache.org/downloads.html) archive, you can extract it in your home directory or anywhere, for that matter, but don't forget to add the $SPARK_HOME environment variable by adding at the end of your ~/.bashrc file : "export SPARK_HOME=<YOUR PATH TO THE EXTRACTED SPARK DIRECTORY>", for example : "export SPARK_HOME=/home/myself/Bureau/Demo/Softs/spark-1.6.1-bin-hadoop2.6".

+ Node and npm are managed by the maven build process : you do not need to install it yourself.

#### Installation

* Before building the project, you need to clone and install the [Spring integration package](https://github.com/ozwillo/ozwillo-java-spring-integration) :

```
git clone https://github.com/ozwillo/ozwillo-java-spring-integration.git
cd ozwillo-java-spring-integration
git checkout ozwillo-java-spring-integration-1.24
./gradlew install
```

* Build oz-energy-aggregations subproject :

```
cd oz-energy-aggregations
sbt publish
```
It will create a fat jar using sbt-assembly and publish it into your maven-repository.

* Go in the [oz-energy](https://github.com/ozwillo/ozwillo-ozenergy/tree/master/oz-energy) subproject and copy the application.model.yml file into application.yml.

```
cd oz-energy
cp config/application.model.yml config/application.yml
```
Then adapt it if you feel the need to, but it should be fine as is if you have set the SPARK_HOME environment variable as told above.

To put it in production, also set : (noauth)devmode props to false, kernel.base_uri and your application credentials (client_*) as appropriate for the chosen target Ozwillo environment, and application.url.

* Then do (in the oz-energy subproject) :

```
mvn clean package
```

#### Running Oz'Energy

Run Spring Boot :

```
mvn spring-boot:run -DrunAggregation
```
(remove the last parameter to avoid running Spark aggregations on startup in addition to nightly)

Open [http://localhost:8080/](http://localhost:8080/) with your favorite browser.

If devmode has been kept to true, various sample data will be displayed for users if they don't have their own energy consumption contract defined in the Datacore yet.
