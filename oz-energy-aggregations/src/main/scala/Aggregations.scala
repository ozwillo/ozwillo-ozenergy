import com.mongodb.spark._ // To enable MongoDB Connector specifics functions  and implicits for the SparkContext and RDD

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._ // To convert some unsupported types (e.g. Lists) from Scala into native types Java
import org.bson.Document

import org.apache.log4j.{Level, Logger}

object Aggregations extends Serializable with AvgByDayAndCK with AvgByDayAndCity with SumByDayAndCK with City {
	def main(args: Array[String]) {
	
	  val arg0 = args.headOption.getOrElse("none")
  	val inputUri: String = "mongodb://127.0.0.1/datacore.oasis.sandbox.enercons:EnergyConsumption_0?readPreference=secondaryPreferred"
  	val outputUri: String = "mongodb://127.0.0.1/datacore1.avgDayAndCK"
  	
  	val conf = new SparkConf()
  		.setAppName("Aggregations")
  		.set("spark.app.id", "Aggregations")
  		.set("spark.mongodb.input.uri", inputUri)
  		.set("spark.mongodb.output.uri", outputUri)
  	
  	val sc = new SparkContext(conf)
	  
	  // To avoid displaying to much information
	  val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)
	  
    def badArgs() = {
      println("----------------------------------------")
  	  println("Bad argument(s)")
  	  println("----------------------------------------")
	  }
    
	  if (args.length == 0) {
	    println("----------------------------------------")
  	  println("No argument")
  	  println("----------------------------------------")
	  } else if (args(0) == "all") {
  	  avgByDayAndCK(sc)
  	  sumByDayAndCK(sc)
  	  if (args.length > 1 && args(1) == "cities") {
    	  val cities = getCities(sc)
    	  for (city <- cities) {
    	    avgByDayAndCity(sc, city)
    	  }
  	  } else {
  	    avgByDayAndCity(sc, "Paris")
  	    avgByDayAndCity(sc, "Lyon")
  	  }
  	  
  	} else if (args(0) == "avg") {
  	  if (args.length < 3) {
    	  badArgs()
  	  } else if (args(1) == "day" && args(2)=="ck") {
  	    avgByDayAndCK(sc)
  	  } else if (args(1) == "day" && args(2) == "city") {
  	    if (args.length < 4) {
  	      badArgs()
  	    } else {
  	      avgByDayAndCityFromScratch(sc, args(3))
  	    }
  	  
  	  } else {
  	    //nothing
  	  }
  	
  	} else if (args(0) == "sum") {
  	  if (args.length < 3) {
    	  badArgs()
  	  } else if (args(1) == "day" && args(2) == "ck") {
  	    sumByDayAndCK(sc)
  	  }
	  } else {
	    badArgs()
  	}
	
	
	
	}
}
