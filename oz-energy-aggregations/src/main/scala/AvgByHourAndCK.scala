import org.apache.spark.rdd.RDD

import com.mongodb.spark._ // To enable MongoDB Connector specifics functions  and implicits for the SparkContext and RDD

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._ // To convert some unsupported types (e.g. Lists) from Scala into native types Java
import org.bson.Document

import java.util.Date
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.LocalDate

trait AvgByHourAndCK {
  
  /** Returns the average consumption per hour for each consumer
   * 
   * @param sc the context for Spark
   * @return a rdd for the average consumption
   */
  def avgByHourAndCK(sc: SparkContext): org.apache.spark.rdd.RDD[org.bson.Document] = {
    
    //For compatibility with MongoDB 2.6, don't use MongoDefaultPartitioner nor MongoSamplePartitioner
    val readConfig = ReadConfig(Map("partitioner" -> "MongoPaginateBySizePartitioner"), Some(ReadConfig(sc)))
    val rdd = MongoSpark.load(sc, readConfig)
    
	  val rddDate = rdd.map(doc => (doc.get("_p").asInstanceOf[org.bson.Document].get("enercons:contract"), 
	      Date.from(LocalDateTime.parse(LocalDateTime.ofInstant(doc.get("_p").asInstanceOf[org.bson.Document]
	        .get("enercons:date").asInstanceOf[Date].toInstant, ZoneId.systemDefault).toLocalDate().toString()
	        +"T" + doc.get("_p").asInstanceOf[org.bson.Document].get("enercons:date").asInstanceOf[Date].toInstant
	        .toString.substring(11,13) + ":00:00").atZone(ZoneId.systemDefault).toInstant), 
	      1, 
	      if (doc.get("_p").asInstanceOf[org.bson.Document].getDouble("enercons:globalKWH").getClass.toString() == "class java.lang.Integer") 
	        doc.get("_p").asInstanceOf[org.bson.Document].getInteger("enercons:globalKWH").toDouble.asInstanceOf[java.lang.Double] 
	      else doc.get("_p").asInstanceOf[org.bson.Document].getDouble("enercons:globalKWH").asInstanceOf[java.lang.Double]))
    
    val rddDateRedByKey = rddDate.map(r => ((r._1, r._2), r)).reduceByKey((a,b) => (a._1, a._2, a._3+b._3, a._4+b._4))
    val avgRdd = rddDateRedByKey.map(a => (a._1, a._2._4/a._2._3)).map(a => (a._1._2,a)).sortByKey(true, 1).map(a => a._2)
    val resAvg = avgRdd.map(t => new Document("contract", t._1._1).append("date", t._1._2).append("globalKW", t._2))
    
    resAvg
  }
}