# ozwillo-ozenergy-aggregations

A scala application for submitting spark job, which executes one or many aggregation of a MongoDB database.

## Installation requirements

+ A priori, it is not necessary to install Scala separately, since sbt can install the most appropriate version itself. So go on the [sbt 0.13](http://www.scala-sbt.org/0.13/docs/Setup.html) link and follow the installation instructions.

+ Download the [**Spark 1.6.1** (pre-built for Hadoop 2.6)](http://spark.apache.org/downloads.html) archive, Then extract it in your home directory or anywhere, for that matter, but don't forget to add the $SPARK_HOME environment variable by adding at the end of your ~/.bashrc file : "export SPARK_HOME=<YOUR PATH TO THE EXTRACTED SPARK DIRECTORY>", for example : "export SPARK_HOME=/home/myself/Bureau/Demo/Softs/spark-1.6.1-bin-hadoop2.6".

+ If you want to be able to edit/debug the code, you will need to use the Scala-IDE (based on Eclipse) : to install it, follow the instructions [here](http://scala-ide.org/download/sdk.html). Extract the archive anywhere you like, open the extracted folder and execute the "eclipse" file.

## How to build for use by oz-energy

> Please follow the installion requirements above, then execute the following instructions.

Go into oz-energy-aggregation and build the project (this will create a fat jar using sbt-assembly and publish it into your local maven-repository) :

```bash
cd oz-energy-aggregations
sbt publish
```

## How to build-run-debug the code

> Please follow the installion requirements above, then execute the following instructions.

+ Go to the folder containing this README.md (ozwillo-ozenergy/oz-energy-aggregations) open a terminal here and type :

```bash
sbt eclipse
```

+ You can then open the Scala-IDE that you previously downloaded, use File → Import → General/Existing Project into Workspace. Select the directory containing your project as root directory, select the project and hit Finish ([original instructions](http://scala-ide.org/docs/current-user-doc/gettingstarted/index.html)).

+ Configure and start Spark (eventually change the config depending on your wishes) :

```bash
export SPARK_MASTER_IP=localhost
export SPARK_MASTER_PORT=7077
$SPARK_HOME/sbin/start-master.sh
```

+ **Note:** If you need a quick introduction/update on Scala, go [here](https://learnxinyminutes.com/docs/scala/).
