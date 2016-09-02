# ozwillo-ozenergy
OzEnergy - energy consumption monitoring for consumers, providers and territories alike

![Demo](https://github.com/ozwillo/ozwillo-ozenergy/blob/master/app-overview/oz-energy.gif)

## Prerequisites

Building requires [**Java 8**](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [**Maven 3**](https://maven.apache.org/download.cgi), [**Node 6**](https://nodejs.org/en/download/releases/) (tested with v6.2.0 and v6.3.0), [**Scala 2.10** or later] (http://www.scala-lang.org/download/2.11.0.html) , [**sbt 0.13**](http://www.scala-sbt.org/0.13/docs/Setup.html) and [**Spark 1.6.1** (pre-built for Hadoop 2.6)](http://spark.apache.org/downloads.html).

Running requires also [**MongoDB 2.6**](https://docs.mongodb.com/v2.6/installation/).

## Installation

* Before building the project, you need to clone and install the [Spring integration package](https://github.com/ozwillo/ozwillo-java-spring-integration) :

```
git clone git@github.com:ozwillo/ozwillo-java-spring-integration.git
cd ozwillo-java-spring-integration
git checkout ozwillo-java-spring-integration-1.24
./gradlew install
```

* Build oz-energy-aggregations subproject :

First open the build.sbt file in the and replace [oz-energy-aggregations](https://github.com/ozwillo/ozwillo-ozenergy/tree/master/oz-energy-aggregations) subproject '/home/charge' by your own home directory.

Then build the project :

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
then adapt it as needed, notably set spark.home if you haven't set the SPARK_HOME environment variable.

To put it in production, also set : (noauth)devmode props to false, kernel.base_uri and your application credentials (client_*) as appropriate for the chosen target Ozwillo environment, and application.url.

* In the file ozwillo-ozenergy/oz-energy/src/main/java/org/ozwillo/energy/spark/EnergyAggregationServices.java, replace SparkHome value and mavenRepository by your own paths.

* Then do (in the oz-energy subproject) : 

```
mvn clean package
```

## Running Oz'Energy 

Run Spring Boot :

```
mvn spring-boot:run -DrunAggregation
```
(remove the last parameter to avoid running Spark aggregations on startup in addition to nightly)

Open [http://localhost:8080/](http://localhost:8080/) with your favorite browser.
