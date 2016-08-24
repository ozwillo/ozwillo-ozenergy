import com.mongodb.spark._

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import org.bson.Document

import java.util.Date

trait AvgByCity extends Util with SumByDayAndContract with ByMonthAndContract with ByYearAndContract{
  
  def filterData(sc: SparkContext, city: String, rdd: com.mongodb.spark.rdd.MongoRDD[org.bson.Document])
    : org.apache.spark.rdd.RDD[org.bson.Document] = {
    
    //get persid data from Mongo Datacore
	  val readConfigPersid = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.org_1.persid:Identity_0?readPreference=secondaryPreferred", 
	      "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val persidRdd = MongoSpark.load(sc, readConfigPersid)
	  
	  //get contract data from Mongo Datacore
  	val readConfigContract = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.oasis.sandbox.enercontr:EnergyConsumptionContract_0?readPreference=secondaryPreferred", 
  	    "partitioner" -> "MongoPaginateBySizePartitioner"))
  	val contractRdd = MongoSpark.load(sc, readConfigContract)
  	
  	val filteredPersidRdd = persidRdd.filter(doc => doc.get("_p").asInstanceOf[org.bson.Document]
  	  .get("adrpost:country").asInstanceOf[String].contains("FR"))
  	  .filter(doc => doc.get("_p").asInstanceOf[org.bson.Document]
  	  .get("adrpost:postName").asInstanceOf[String].contains(city))
  	                                
  	//join persid and contract to find the contracts in the city
  	val mappedPersidRdd = filteredPersidRdd.map(persid => (persid.get("_uri").asInstanceOf[String], persid)) //(persid._uri, persid)
  	val mappedContractRdd = contractRdd.map(contract => (contract.get("_p").asInstanceOf[org.bson.Document]
  	  .get("enercontr:consumer").asInstanceOf[String], contract)) // (persid._uri, contract)
  	val joinPersidContractRdd = mappedPersidRdd.join(mappedContractRdd) //(persid._uri, (persid, contract))
  	
  	// join the former result with consumption to find the data
		val mappedPersidContractRdd = joinPersidContractRdd.map(r => (r._2._2.get("_uri").asInstanceOf[String], r._2._2)) //(contract._uri, contract)
		val mappedConsRdd = rdd.map(res => (res.get("contract").asInstanceOf[String], res)) //(contract._uri, cons)
	  val joinConsRdd = mappedPersidContractRdd.join(mappedConsRdd).map(a => a._2._2)
		
	  //Then aggregate the data
	  val cityAvg = joinConsRdd.map(doc => (doc.get("date").asInstanceOf[Date], 
	      1, 
	      if (doc.get("globalKW").getClass.toString() == "class java.lang.Integer") 
	        doc.getInteger("globalKW").toDouble.asInstanceOf[java.lang.Double] 
	      else doc.getDouble("globalKW").asInstanceOf[java.lang.Double]))
                      	  .map(a => (a._1, a))
                      	  .reduceByKey((a,b) => (a._1, a._2+b._2, a._3+b._3))
                      	  .map(a => (a._1, a._2._3/a._2._2))
	  val res = cityAvg.map(t => new Document("contract", "").append("date", t._1).append("globalKW", t._2))
		
		res
  }
  
  
  /** Determines and saves the average of the daily consumption in a city
   * 
   * Assumes the collection "sumDayAndContract" is up to date
   * 
   * @param sc the context for Spark
   * @param city the city for which the aggregation is done
   */
  def avgByDayAndCity(sc: SparkContext, city: String) = {
    //get the results from sumByDayAndContract from the database
    val readConfig = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore1.sumDayAndContract", 
	      "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val resSum = MongoSpark.load(sc, readConfig)
    
	  val res = filterData(sc, city, resSum)
	  
	  //Clear collection before saving
	  MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection(cityCollection(city, "avgDayFor")).drop()})
	  
	  //Save
	  res.saveToMongoDB(writeConfigCity(city, "avgDayFor"))
  }
  
  /** Determines and saves the average of the daily consumption in a city
   * 
   * Re-runs all the necessary previous aggregations
   * 
   * @param sc the context for Spark
   * @param city the city for which the aggregation is done
   */
  def avgByDayAndCityFromScratch(sc: SparkContext, city: String) = {
    sumByDayAndContract(sc)
    avgByDayAndCity(sc, city)
  }
  
  /** Determines and saves the average of the monthly consumption in a city
   * 
   * Assumes the collection "sumMonthAndContract" is up to date
   * 
   * @param sc the context for Spark
   * @param city the city for which the aggregation is done
   */
  def avgByMonthAndCity(sc: SparkContext, city: String) = {
    //get the results from sumByDayAndContract from the database
    val readConfig = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore1.sumMonthAndContract", 
	      "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val resSum = MongoSpark.load(sc, readConfig)
    
	  val res = filterData(sc, city, resSum)
	  
	  //Clear collection before saving
	  MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection(cityCollection(city, "avgMonthFor")).drop()})
	  //Save
	  res.saveToMongoDB(writeConfigCity(city, "avgMonthFor"))
  }
  
  /** Determines and saves the average of the monthly consumption in a city
   * 
   * Re-runs all the necessary previous aggregations
   * 
   * @param sc the context for Spark
   * @param city the city for which the aggregation is done
   */
  def avgByMonthAndCityFromScratch(sc: SparkContext, city: String) = {
    sumByMonthAndContractFromScratch(sc)
    avgByMonthAndCity(sc, city)
  }
  
  /** Determines and saves the average of the annual consumption in a city
   * 
   * Assumes the collection "sumYearAndContract" is up to date
   * 
   * @param sc the context for Spark
   * @param city the city for which the aggregation is done
   */
  def avgByYearAndCity(sc: SparkContext, city: String) = {
    //get the results from sumByDayAndContract from the database
    val readConfig = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore1.sumYearAndContract", 
	      "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val resSum = MongoSpark.load(sc, readConfig)
    
	  val res = filterData(sc, city, resSum)
	  
	  //Clear collection before saving
	  MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection(cityCollection(city, "avgYearFor")).drop()})
	  //Save
	  res.saveToMongoDB(writeConfigCity(city, "avgYearFor"))
  }
  
  /** Determines and saves the average of the annual consumption in a city
   * 
   * Re-runs all the necessary previous aggregations
   * 
   * @param sc the context for Spark
   * @param city the city for which the aggregation is done
   */
  def avgByYearAndCityFromScratch(sc: SparkContext, city: String) = {
    sumByYearAndContractFromScratch(sc)
    avgByYearAndCity(sc, city)
  }
 
}