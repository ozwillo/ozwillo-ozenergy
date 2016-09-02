# ozwillo-ozenergy
OzEnergy - energy consumption monitoring for consumers, providers and territories alike

## Prerequisites

Building requires **Java 8**, **Maven 3** and **Node 6** (tested with v6.2.0).

## Installation

* Before building the project, you need to clone and install the [Spring integration package](https://github.com/ozwillo/ozwillo-java-spring-integration) :

```
git clone git@github.com:ozwillo/ozwillo-java-spring-integration.git
cd ozwillo-java-spring-integration
git checkout ozwillo-java-spring-integration-1.24
./gradlew install
```

* Go in the oz-energy subproject and copy the application.model.yml file into application.yml.

```
cd oz-energy
cp config/application.model.yml config/application.yml
```
then adapt it as needed (such as setting credentials that are appropriate to the chosen target Ozwillo environment).

* Then do (in the oz-energy subproject) : 

```
mvn clean package 
```

## Running Oz'Energy 

Run Spring Boot :

```
mvn spring-boot:run
```

Open [http://localhost:8080/](http://localhost:8080/) with your favorite browser.
