import com.mongodb.spark._

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import org.bson.Document

import java.util.Date

trait ByYearAndContract extends Util with ByMonthAndContract {
  
  /** Sums the consumption's data from a rdd
   *  
   * @param sc the context for Spark
   * @param rdd the rdd to make sum
   * @return a spark rdd containing the sum
   */
  def rddYear(sc: SparkContext, rdd: com.mongodb.spark.rdd.MongoRDD[org.bson.Document])
    : org.apache.spark.rdd.RDD[((String, java.util.Date), (String, java.util.Date, Int, java.lang.Double))] = {
    
    val rddYear = rdd.map(doc => (doc.getString("contract"), 
        dateFromYearFromDate(doc.get("date").asInstanceOf[java.util.Date]), 
        1, 
        if (doc.get("globalKW").getClass.toString() == "class java.lang.Integer") 
          doc.getInteger("globalKW").toDouble.asInstanceOf[java.lang.Double] 
        else doc.getDouble("globalKW").asInstanceOf[java.lang.Double]))
        
	  val rddYearRedByKey = rddYear.map(r => ((r._1, r._2), r))
	    .reduceByKey((a,b) => (a._1, a._2, a._3+b._3, a._4+b._4))
	    
	  rddYearRedByKey
  }
  
  /** Determines the average of the daily consumption over a year
   *  from the collection "avgMonthAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Assumes the collection "avgMonthAndContract" is up to date
   *  
   * @param sc the context for Spark
   */
  def avgByYearAndContractPerDay(sc: SparkContext) = {
    val aggregationMongoIP: String = sc.getConf.get("aggregationMongoIP");
	  val aggregationMongoId: String = sc.getConf.get("aggregationMongoId");
	  
	  val aggregationURIRead: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".avgMonthAndContract";
  	    
    val readConfig = ReadConfig(Map("uri" -> aggregationURIRead, 
        "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val rdd = MongoSpark.load(sc, readConfig)
	  
	  val rddYearRedByKey = rddYear(sc, rdd)
	  
	  val finalRdd = rddYearRedByKey.map(a => (a._1, a._2._4/a._2._3))
	  val res = finalRdd.map(t => new Document("contract", t._1._1.asInstanceOf[String])
	    .append("date", t._1._2).append("globalKW", t._2))

	  //To have an empty output collection
	  MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection("avgYearAndContract").drop()})
	  //Saves the collection
	  val aggregationURIWrite: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".avgYearAndContract";
	  
	  val writeConfig = WriteConfig(Map("uri" -> aggregationURIWrite))
	  res.saveToMongoDB(writeConfig)
  }
  
  
  /** Determines the average of the daily consumption over a year
   *  from the collection "avgMonthAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Re-runs all the necessary previous aggregations
   *  
   * @param sc the context for Spark
   */
  def avgByYearAndContractPerDayFromScratch(sc: SparkContext) = {
    avgByMonthAndContractFromScratch(sc)
    avgByYearAndContractPerDay(sc)
  }
  
  
  /** Determines the sum of the consumption over a year
   *  from the collection "sumMonthAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Assumes the collection "sumMonthAndContract" is up to date
   *  
   * @param sc the context for Spark
   */
  def sumByYearAndContract(sc: SparkContext) = {
    val aggregationMongoIP: String = sc.getConf.get("aggregationMongoIP");
	  val aggregationMongoId: String = sc.getConf.get("aggregationMongoId");
	  
	  val aggregationURIRead: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".sumMonthAndContract";
  	
    val readConfig = ReadConfig(Map("uri" -> aggregationURIRead, 
        "partitioner" -> "MongoPaginateBySizePartitioner"))
	  val rdd = MongoSpark.load(sc, readConfig)
	  
	  val rddYearRedByKey = rddYear(sc, rdd)
	  val res = rddYearRedByKey.map(t => new Document("contract", t._1._1.asInstanceOf[String])
	    .append("date", t._1._2).append("globalKW", t._2._4))
	  
	  //To have an empty output collection
  	MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection("sumYearAndContract").drop()})
  	//Saves the collection
	  val aggregationURIWrite: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".sumYearAndContract";
  	
  	val writeConfig = WriteConfig(Map("uri" -> aggregationURIWrite))
  	res.saveToMongoDB(writeConfig)
  }
  
  /** Determines the sum of the consumption over a year
   *  from the collection "sumMonthAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Re-runs all the necessary previous aggregations
   *  
   * @param sc the context for Spark
   */
  def sumByYearAndContractFromScratch(sc: SparkContext) = {
    sumByMonthAndContractFromScratch(sc)
    sumByYearAndContract(sc)
  }
  
  
}