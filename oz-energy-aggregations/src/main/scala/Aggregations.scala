import com.mongodb.spark._ // To enable MongoDB Connector specifics functions  and implicits for the SparkContext and RDD

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._ // To convert some unsupported types (e.g. Lists) from Scala into native types Java
import org.bson.Document

import org.apache.log4j.{Level, Logger}

import java.io._
import org.apache.log4j.Appender
import org.apache.log4j.FileAppender
import java.util.regex.Pattern
import org.apache.log4j.PatternLayout

object Aggregations extends Serializable with AvgByDayAndContract with AvgByCity
with SumByDayAndContract with City with ByMonthAndContract with ByYearAndContract {
	val usage = """
      Usage is :
        Aggregations --datacore-mongo-IP ozwillo-mongo-1,ozwillo-mongo-3  --datacore-mongo-id datacore --aggregation-mongo-IP 127.0.0.1 --aggregation-mongo-id datacore1 --aggregation-type all [--all-cities]
        Aggregations --datacore-mongo-IP ozwillo-mongo-1,ozwillo-mongo-3  --datacore-mongo-id datacore --aggregation-mongo-IP 127.0.0.1 --aggregation-mongo-id datacore1 --aggregation-type avg --groupBy-time day/month/year --groupBy-otherDimension contract
        Aggregations --datacore-mongo-IP ozwillo-mongo-1,ozwillo-mongo-3  --datacore-mongo-id datacore --aggregation-mongo-IP 127.0.0.1 --aggregation-mongo-id datacore1 --aggregation-type avg --groupBy-time day/month/year --groupBy-otherDimension city --city Lyon
        Aggregations --datacore-mongo-IP ozwillo-mongo-1,ozwillo-mongo-3  --datacore-mongo-id datacore --aggregation-mongo-IP 127.0.0.1 --aggregation-mongo-id datacore1 --aggregation-type sum --groupBy-time day/month/year --groupBy-otherDimension contract
    """

	def main(args: Array[String]) {
		// To avoid displaying to much information
		val rootLogger = Logger.getRootLogger()
    rootLogger.setLevel(Level.ERROR)

    val fa: FileAppender = new FileAppender();
		val pl: PatternLayout = new PatternLayout("%d %-5p [%c{1}] %m%n");
    fa.setName("FileLogger");
    fa.setFile("aggregations.log");
    fa.setLayout(pl);
    fa.setThreshold(Level.ERROR);
    fa.setAppend(true);
    fa.activateOptions();

    Logger.getRootLogger().addAppender(fa);

    rootLogger.error("Aggregations Logger is up and running.");

    // ----------- BEGIN -----------
		// Arguments to Options map
    // -----------------------------
    val arglist = args.toList
    type OptionMap = Map[Symbol, String]

		// Transform command in option map, easier to read
    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map // Stop condition : when end of command is reached, return the option map
        case "--aggregation-type" :: value :: tail =>
                               nextOption(map ++ Map('aggregationType -> value), tail)
        case "--all-cities" :: tail =>
                               nextOption(map ++ Map('allCities -> "true"), tail)
        case "--groupBy-time" :: value :: tail =>
                               nextOption(map ++ Map('groupByTime -> value), tail)
        case "--groupBy-otherDimension" :: value :: tail =>
                               nextOption(map ++ Map('groupByOtherDimension -> value), tail)
        case "--city" :: value :: tail =>
                               nextOption(map ++ Map('city -> value), tail)
        case "--datacore-mongo-IP" :: value :: tail =>
                               nextOption(map ++ Map('datacoreMongoIP -> value), tail)
        case "--datacore-mongo-id" :: value :: tail =>
                               nextOption(map ++ Map('datacoreMongoId -> value), tail)
        case "--aggregation-mongo-IP" :: value :: tail =>
                               nextOption(map ++ Map('aggregationMongoIP -> value), tail)
        case "--aggregation-mongo-id" :: value :: tail =>
                               nextOption(map ++ Map('aggregationMongoId -> value), tail)
        case option :: tail => rootLogger.error("Unknown option " + option)
                               exit(1)
      }
    }
    val options = nextOption(Map(),arglist)

    rootLogger.debug(options.toString());
    // ------------ END ------------
		// Arguments to Options map
    // -----------------------------

    // ----------- BEGIN -----------
		// Options coherence check
    // -----------------------------
    /** Prints message in case of bad argument(s) */
    def badArgs() = {
  	  rootLogger.error("Please refer to the following USAGE :")
      rootLogger.error(usage)
      exit(1)
	  }

    // If no args -> Error
		if (options.isEmpty) {
		  rootLogger.error("No arguments specified.");
		  badArgs()
		}
		else {
		  // If no mention of datacore and aggregation dbs, or if no aggregation type -> Error
		  if (!(options.contains('datacoreMongoIP) && options.contains('datacoreMongoId)
		      && options.contains('aggregationMongoIP) && options.contains('aggregationMongoId)
		      && options.contains('aggregationType))) {
		    rootLogger.error("You forgot to specify either the datacore mongo IP/Id, the aggregation mongo IP/Id or the aggregation type.");
		    badArgs()
		  }
		  else {
		    if (options('aggregationType) == "all") {
		      // If try to mention groupby or city when all aggregations -> Error
		      if (options.contains('groupByTime) || options.contains('groupByOtherDimension)
		          || options.contains('city)) {
		        rootLogger.error("You cannot specify a --groupByTime or --groupByOtherDimension in this context.");
		        badArgs()
		      }
		    }
		    else if (options('aggregationType) == "avg") {
		      // If try specific aggregation type without groupby -> Error
		      if (!(options.contains('groupByTime) && options.contains('groupByOtherDimension))) {
		        rootLogger.error("You cannot aggregate without specifying both --groupBy<>.");
		        badArgs()
		      }
		      // If try groupByTime isn't of an authorized type -> Error
		      else if (!(options('groupByTime) == "day" || options('groupByTime) == "month"
		          || options('groupByTime) == "year")) {
		        rootLogger.error("The time measure you entered is not valid.");
		        badArgs()
		      }
		      // If try groupByOtherDimension isn't of an authorized type -> Error
		      else if (!(options('groupByOtherDimension) == "city" || options('groupByOtherDimension) == "contract")) {
		        rootLogger.error("The other dimension measure is not valid.");
		        badArgs()
		      }
		      // If we want to group by city but no city is specified
		      else if (options('groupByOtherDimension) == "city" && !options.contains('city)) {
		        rootLogger.error("You must specify an existing city name if you wish to aggregate only for this specific city.");
		        badArgs()
		      }
		    }
		    else if (options('aggregationType) == "sum") {
		      // If try specific aggregation type without groupby -> Error
		      if (!(options.contains('groupByTime) && options.contains('groupByOtherDimension))) {
		        rootLogger.error("You cannot aggregate without specifying both --groupBy<>.");
		        badArgs()
		      }
		      // If try groupByTime isn't of an authorized type -> Error
		      else if (!(options('groupByTime) == "day" || options('groupByTime) == "month"
		          || options('groupByTime) == "year")) {
		        rootLogger.error("The time measure you entered is not valid.");
		        badArgs()
		      }
		      // If try groupByOtherDimension isn't of an authorized type -> Error
		      else if (!(options('groupByOtherDimension) == "contract")) {
		        rootLogger.error("The other dimension measure is not valid.");
		        badArgs()
		      }
		      // If try to mention city -> Error
		      else if (options.contains('city)) {
		        rootLogger.error("You cannot specify a city in this context.");
		        badArgs()
		      }
		    }
		  }
		}
    // ------------ END ------------
		// Options coherence check
    // -----------------------------

    // ----------- BEGIN -----------
		// Spark config
    // -----------------------------
		val energyProject: String = "energy_analysis_0";
		val energyContractCollection: String = "enercontr:EnergyConsumptionContract_0";
		val energyConsumptionCollection: String = "enercons:EnergyConsumption_0";
		val mongoParameters: String = "readPreference=secondary";

 		val inputUri: String = "mongodb://" + options('datacoreMongoIP) + "/" + options('datacoreMongoId) + "." + energyProject + "." + energyConsumptionCollection + "?" + mongoParameters
		val outputUri: String = "mongodb://"+ options('aggregationMongoIP) + "/" + options('aggregationMongoId) + ".avgDayAndContract"

		val conf = new SparkConf()
		  .setAppName("Aggregations")
		  .set("spark.app.id", "Aggregations")
		  .set("spark.mongodb.input.uri", inputUri)
		  .set("spark.mongodb.output.uri", outputUri)
		  .set("datacoreMongoIP", options('datacoreMongoIP))
		  .set("datacoreMongoId", options('datacoreMongoId))
		  .set("aggregationMongoIP", options('aggregationMongoIP))
		  .set("aggregationMongoId", options('aggregationMongoId))
		  .set("energyProject", energyProject)
		  .set("energyContractCollection", energyContractCollection)
		  .set("energyConsumptionCollection", energyConsumptionCollection)
		  .set("mongoParameters", mongoParameters)
		val sc = new SparkContext(conf)
    // ------------ END ------------
		// Spark config
    // -----------------------------

    // ----------- BEGIN -----------
		// Aggregation execution
    // -----------------------------
	  if (options('aggregationType) == "all") {
  	  avgByDayAndContract(sc)
  	  sumByDayAndContract(sc)
  	  avgByMonthAndContract(sc)
  	  sumByMonthAndContract(sc)
  	  avgByYearAndContractPerDay(sc)
  	  sumByYearAndContract(sc)
  	  if (options('allCities) == "true") {
    	  val cities = getCities(sc)
    	  rootLogger.error("Cities : " + cities.mkString(" "));
    	  for (city <- cities) {
    	    avgByDayAndCity(sc, city)
    	    avgByMonthAndCity(sc, city)
    	  }
  	  } else {
  	    avgByDayAndCity(sc, "Paris")
  	    avgByDayAndCity(sc, "Lyon")
  	    avgByMonthAndCity(sc, "Paris")
  	    avgByMonthAndCity(sc, "Lyon")
  	    avgByYearAndCity(sc, "Paris")
  	    avgByYearAndCity(sc, "Lyon")
  	  }
  	}
	  else if (options('aggregationType) == "avg") {
  	  if (options('groupByTime) == "day" && options('groupByOtherDimension) == "contract") {
  	    avgByDayAndContract(sc)
  	  } else if (options('groupByOtherDimension) == "city") {
        if (options('groupByTime) == "day") {
  	      avgByDayAndCityFromScratch(sc, options('city))
  	    } else if (options('groupByTime) == "month") {
  	      avgByMonthAndCityFromScratch(sc, options('city))
  	    } else if (options('groupByTime) == "year") {
  	      avgByYearAndCityFromScratch(sc, options('city))
  	    }
  	  } else if (options('groupByTime) == "month" && options('groupByOtherDimension) == "contract") {
  	    avgByMonthAndContractFromScratch(sc)
  	  } else if (options('groupByTime) == "year" && options('groupByOtherDimension) == "contract") {
  	    avgByYearAndContractPerDayFromScratch(sc)
  	  }
  	}
	  else if (options('aggregationType) == "sum") {
  	  if (options('groupByTime) == "day" && options('groupByOtherDimension) == "contract") {
  	    sumByDayAndContract(sc)
  	  } else if (options('groupByTime) == "month" && options('groupByOtherDimension) == "contract") {
  	    sumByMonthAndContractFromScratch(sc)
  	  } else if (options('groupByTime) == "year" && options('groupByOtherDimension) == "contract") {
  	    sumByYearAndContractFromScratch(sc)
  	  }
	  }
    // ----------- END -----------
		// Aggregation execution
    // -----------------------------
	}
}
