import com.mongodb.spark._

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import org.bson.Document

import java.util.Date
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.LocalDate

trait SumByDayAndCK extends Util with AvgByHourAndCK {
  
  /** Determines and saves the total consumption per day and customer
   * 
   * @param sc the context for Spark
   */
  def sumByDayAndCK(sc: SparkContext) = {
    
    val writeConfig = WriteConfig(Map("uri" -> "mongodb://127.0.0.1/datacore1.sumDayAndCK"))
    //To have an empty output collection
	  MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection("sumDayAndCK").drop()})
	
    val resAvg = avgByHourAndCK(sc)
    val rddDay = resAvg.map(doc => (doc.getString("contract"), 
        dateFromStringdate(LocalDateTime.ofInstant(doc.get("date").asInstanceOf[Date].toInstant, ZoneId.systemDefault).toLocalDate().toString()), 
        if (doc.get("globalKW").getClass.toString() == "class java.lang.Integer") 
          doc.getInteger("globalKW").toDouble.asInstanceOf[java.lang.Double] 
        else doc.getDouble("globalKW").asInstanceOf[java.lang.Double]))

    val sumRdd = rddDay.map(r => ((r._1, r._2), r)).reduceByKey((a,b) => (a._1, a._2, a._3+b._3))
	
	  val resSum = sumRdd.map(t => new Document("contract", t._1._1).append("date", t._1._2).append("globalKW", t._2._3))
	
	  resSum.saveToMongoDB(writeConfig)
  }
}