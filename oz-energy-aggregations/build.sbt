import sbt._
import Keys._
import sbtassembly.AssemblyPlugin.autoImport._

name := "oz-energy-aggregations"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies +="org.mongodb" % "mongo-java-driver" % "3.3.0" 

libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided"

libraryDependencies += "org.mongodb.spark" % "mongo-spark-connector_2.10" % "1.0.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.1" % "provided"

lazy val app = (project in file(".")).
  settings(
    artifact in (Compile, assembly) ~= { art =>
      art.copy(`classifier` = Some("assembly"))
    }
  ).
  settings(addArtifact(artifact in (Compile, assembly), assembly).settings: _*)

publishTo := Some(Resolver.file("file",  new File("/home/charge"+"/.m2/repository/spark_aggregations")))


