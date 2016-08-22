import com.mongodb.spark._ // To enable MongoDB Connector specifics functions  and implicits for the SparkContext and RDD

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._ // To convert some unsupported types (e.g. Lists) from Scala into native types Java
import org.bson.Document


trait City {
  def getCities(sc: SparkContext): Array[String] = {
    //get geo data from Mongo Datacore
  	val readConfigGeo = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.geo_1.geo:Area_0?readPreference=secondaryPreferred", "partitioner" -> "MongoPaginateBySizePartitioner"))
  	val geoRdd = MongoSpark.load(sc, readConfigGeo)
  	
  	//get persid data from Mongo Datacore
  	val readConfigPersid = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.org_1.persid:Identity_0?readPreference=secondaryPreferred", "partitioner" -> "MongoPaginateBySizePartitioner"))
  	val persidRdd = MongoSpark.load(sc, readConfigPersid)
  	
  	//get contract data from Mongo Datacore
  	val readConfigContract = ReadConfig(Map("uri" -> "mongodb://127.0.0.1/datacore.oasis.sandbox.enercontr:EnergyConsumptionContract_0?readPreference=secondaryPreferred", "partitioner" -> "MongoPaginateBySizePartitioner"))
  	val contractRdd = MongoSpark.load(sc, readConfigContract)
  	
  	
  	//join persid and contract to find the contract in the city Lyon
  	val mappedPersidRdd = persidRdd.map(persid => (persid.get("_uri").asInstanceOf[String], persid)) //(persid._uri, persid)
  	val mappedContractRdd = contractRdd.map(contract => (contract.get("_p").asInstanceOf[org.bson.Document].get("enercontr:consumer").asInstanceOf[String], contract)) // (persid._uri, contract)
  	val joinPersidContractRdd = mappedPersidRdd.join(mappedContractRdd) //(persid._uri, (persid, contract))
  	
  	
  	//join geo with the former result
  	
  	val filteredGeoRdd = geoRdd.filter(doc => doc.get("_p").asInstanceOf[org.bson.Document].get("geo:country")=="http://data.ozwillo.com/dc/type/geocofr:Pays_0/FR").filter(doc => doc.get("_t").asInstanceOf[java.util.ArrayList[Any]].contains("geocifr:Commune_0") == true)
  	val mappedGeoRdd = filteredGeoRdd.map(geo => (geo.get("_uri").asInstanceOf[String], geo.get("_p").asInstanceOf[org.bson.Document].get("geo:name").asInstanceOf[java.util.ArrayList[org.bson.Document]]))
  	val mappedPersidContractRdd = joinPersidContractRdd.map(a => (a._2._1.get("_p").asInstanceOf[org.bson.Document].get("adrpost:postName").asInstanceOf[String], a._2._1.get("_uri").asInstanceOf[String]))
  	val join = mappedPersidContractRdd.join(mappedGeoRdd) //(geo._uri, (persid, geo))
  	
  	val res = join.map(a => a._2._2.get(0).getString("v")).distinct()
  	
  	res.collect()
  }
}