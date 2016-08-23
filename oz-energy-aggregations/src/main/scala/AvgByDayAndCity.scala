import com.mongodb.spark._

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import org.bson.Document

import java.util.Date

// Find the average of the daily consumption in a city 
trait AvgByDayAndCity extends Util with SumByDayAndCK {
  def avgByDayAndCity(sc: SparkContext, city: String) = {
    //get the results from sumByDayAndCK from the database
    val readConfig = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore1.sumDayAndCK", 
	      "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val resSum = MongoSpark.load(sc, readConfig)
    
    //get persid data from Mongo Datacore
	  val readConfigPersid = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.org_1.persid:Identity_0?readPreference=secondaryPreferred", 
	      "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val persidRdd = MongoSpark.load(sc, readConfigPersid)
	  
	  //get contract data from Mongo Datacore
  	val readConfigContract = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.oasis.sandbox.enercontr:EnergyConsumptionContract_0?readPreference=secondaryPreferred", 
  	    "partitioner" -> "MongoPaginateBySizePartitioner"))
  	val contractRdd = MongoSpark.load(sc, readConfigContract)
  	
  	val filteredPersidRdd = persidRdd.filter(doc => doc.get("_p").asInstanceOf[org.bson.Document].get("adrpost:country").asInstanceOf[String].contains("FR"))
  	                                .filter(doc => doc.get("_p").asInstanceOf[org.bson.Document].get("adrpost:postName").asInstanceOf[String].contains("Lyon"))
  	                                
  	//join persid and contract to find the contract in the city
  	val mappedPersidRdd = filteredPersidRdd.map(persid => (persid.get("_uri").asInstanceOf[String], persid)) //(persid._uri, persid)
  	val mappedContractRdd = contractRdd.map(contract => (contract.get("_p").asInstanceOf[org.bson.Document].get("enercontr:consumer").asInstanceOf[String], contract)) // (persid._uri, contract)
  	val joinPersidContractRdd = mappedPersidRdd.join(mappedContractRdd) //(persid._uri, (persid, contract))
  	
  	// join the former result with consumption to find the data
		val mappedPersidContractRdd = joinPersidContractRdd.map(r => (r._2._2.get("_uri").asInstanceOf[String], r._2._2)) //(contract._uri, contract)
  	val mappedConsRdd = resSum.map(res => (res.get("contract").asInstanceOf[String], res)) //(contract._uri, cons)
	  val joinConsRdd = mappedPersidContractRdd.join(mappedConsRdd).map(a => a._2._2)
	  
	  //Then aggregate the data
	  val cityAvg = joinConsRdd.map(doc => (doc.get("date").asInstanceOf[Date], 1, if (doc.get("globalKW").getClass.toString() == "class java.lang.Integer") doc.getInteger("globalKW").toDouble.asInstanceOf[java.lang.Double] else doc.getDouble("globalKW").asInstanceOf[java.lang.Double]))
                      	  .map(a => (a._1, a))
                      	  .reduceByKey((a,b) => (a._1, a._2+b._2, a._3+b._3))
                      	  .map(a => (a._1, a._2._3/a._2._2))
	  val res = cityAvg.map(t => new Document("contract", "").append("date", t._1).append("globalKW", t._2))
	  
	  //Clear collection before saving
	  MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection(cityCollection(city)).drop()})
	  
	  //Save
	  res.saveToMongoDB(writeConfigCity(city))
	  
  }
  
  def avgByDayAndCityFromScratch(sc: SparkContext, city: String) = {
    sumByDayAndCK(sc)
    avgByDayAndCity(sc, city)
  }
 
}