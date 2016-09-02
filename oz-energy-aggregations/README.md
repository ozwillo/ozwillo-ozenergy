# ozwillo-ozenergy-aggregations

A scala application for submitting spark job, which executes one or many aggregation of a MongoDB database.

## Prerequisites

Building requires **Scala 2.10** and **sbt 0.13**.

If you want to test it, you will also need **Spark 1.6.1*** (pre-built for Hadoop 2.6).

## Installation

* Go into oz-energy-aggregation :

```
cd oz-energy-aggregations
```

* Build the project :

```
sbt publish
```

It will create a fat jar using sbt-assembly and publish it into your local maven-repository.
