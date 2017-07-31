import com.mongodb.spark._

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import org.bson.Document

import java.util.Date

import com.mongodb.MongoClient;

trait ByMonthAndContract extends Util with SumByDayAndContract {
  
  /** Sums the consumption's data from the collection "sumDayAndContract"
   * 
   * @param sc the context for Spark
   * @return a spark rdd for the sum
   */
  def rddMonth(sc: SparkContext): org.apache.spark.rdd.RDD[((String, java.util.Date), (String, java.util.Date, Int, java.lang.Double))] = {
	  val aggregationMongoIP: String = sc.getConf.get("aggregationMongoIP");
	  val aggregationMongoId: String = sc.getConf.get("aggregationMongoId");
	  
	  val aggregationURI: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".sumDayAndContract";
    
    //For compatibility with MongoDB 2.6, don't use MongoDefaultPartitioner nor MongoSamplePartitioner
  	val readConfig = ReadConfig(Map("uri" -> aggregationURI, 
  	    "partitioner" -> "MongoPaginateBySizePartitioner"))
  	
  	val rdd = MongoSpark.load(sc, readConfig)
  	
  	val rddMonth = rdd.map(doc => (doc.getString("contract"), 
  	    dateFromMonthFromDate(doc.get("date").asInstanceOf[java.util.Date]), 
  	    1, 
  	    if (doc.get("globalKW").getClass.toString() == "class java.lang.Integer") 
  	      doc.getInteger("globalKW").toDouble.asInstanceOf[java.lang.Double] 
  	    else doc.getDouble("globalKW").asInstanceOf[java.lang.Double]))
  	
  	val rddMonthRedByKey = rddMonth.map(r => ((r._1, r._2), r))
  	                          .reduceByKey((a,b) => (a._1, a._2, a._3+b._3, a._4+b._4))
  	rddMonthRedByKey                  
  }
  
  
  /** Determines the average of the daily consumption over a month
   *  from the collection "sumDayAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Assumes the collection "sumDayAndContract" is up to date
   *  
   * @param sc the context for Spark
   */
  def avgByMonthAndContract(sc: SparkContext) = {
    
    val rddMonthRedByKey = rddMonth(sc)
  	
  	val finalRdd = rddMonthRedByKey.map(a => (a._1, a._2._4/a._2._3))
  	
  	val res = finalRdd.map(t => new Document("contract", t._1._1.asInstanceOf[String])
  	                                 .append("date", t._1._2)
  	                                 .append("globalKW", t._2))
  
  	//To have an empty output collection
 	  val mongoClient: MongoClient = new MongoClient()
    mongoClient.getDatabase("aggregdb").getCollection("avgMonthAndContract").drop()
  	
	  val aggregationMongoIP: String = sc.getConf.get("aggregationMongoIP");
	  val aggregationMongoId: String = sc.getConf.get("aggregationMongoId");
	  
	  val aggregationURI: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".avgMonthAndContract";
	  
  	val writeConfig = WriteConfig(Map("uri" -> aggregationURI))
  	res.saveToMongoDB(writeConfig)
  }
  
  /** Determines the average of the daily consumption over a month
   *  from the collection "sumDayAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Re-runs all the necessary previous aggregations
   *  
   * @param sc the context for Spark
   */
  def avgByMonthAndContractFromScratch(sc: SparkContext) = {
    sumByDayAndContract(sc)
    avgByMonthAndContract(sc)
  }
  
  
  /** Determines the sum of the consumption over a month
   *  from the collection "sumDayAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Assumes the collection "sumDayAndContract" is up to date
   *  
   * @param sc the context for Spark
   */
  def sumByMonthAndContract(sc: SparkContext) = {
    val rddMonthRedByKey = rddMonth(sc)
    val res = rddMonthRedByKey.map(t => new Document("contract", t._1._1)
      .append("date", t._1._2).append("globalKW", t._2._4))
  	
  	//To have an empty output collection
 	  val mongoClient: MongoClient = new MongoClient()
    mongoClient.getDatabase("aggregdb").getCollection("sumMonthAndContract").drop()
  	
	  val aggregationMongoIP: String = sc.getConf.get("aggregationMongoIP");
	  val aggregationMongoId: String = sc.getConf.get("aggregationMongoId");
	  
	  val aggregationURI: String = "mongodb://" + aggregationMongoIP + "/" + aggregationMongoId + ".sumMonthAndContract";
  	
  	val writeConfig = WriteConfig(Map("uri" -> aggregationURI))
  	res.saveToMongoDB(writeConfig)
  }
  
  /** Determines the sum of the consumption over a month
   *  from the collection "sumDayAndContract" 
   *  and saves it in a MongoDB collection
   *  
   *  Re-runs all the necessary previous aggregations
   *  
   * @param sc the context for Spark
   */
  def sumByMonthAndContractFromScratch(sc: SparkContext) = {
    sumByDayAndContract(sc)
    sumByMonthAndContract(sc)
  }
  
}