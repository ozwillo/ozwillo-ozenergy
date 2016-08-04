# ozwillo-ozenergy
OzEnergy - energy consumption monitoring for consumers, providers and territories alike

## Prerequisites

Building requires **Java 8**, **Maven 3** and **Node 6**.

## Installation

* Before building the project, you need to clone and install the [Spring integration package](https://github.com/ozwillo/ozwillo-java-spring-integration) :

```
git clone git@github.com:ozwillo/ozwillo-java-spring-integration.git
cd ozwillo-java-spring-integration
./gradlew install
```

* Go in the oz-energy subproject and rename the application.model.yml file into application.yml.

```
cd oz-energy
mv config/application.model.yml config/application.yml
``` 

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
