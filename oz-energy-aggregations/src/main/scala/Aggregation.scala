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
import java.time.LocalTime

object Aggregation {
	def main(args: Array[String]) {
	
	val inputUri: String = args.headOption.getOrElse("mongodb://127.0.0.1/datacore.oasis.sandbox.enercons:EnergyConsumption_0?readPreference=secondaryPreferred")
	val outputUri: String = args.headOption.getOrElse("mongodb://127.0.0.1/datacore1.avgDayAndCK")
	
	val conf = new SparkConf()
		.setAppName("Aggregation")
		.set("spark.app.id", "Aggregation")
		.set("spark.mongodb.input.uri", inputUri)
		.set("spark.mongodb.output.uri", outputUri)
	
	val sc = new SparkContext(conf)
	
	//To have an empty output collection
	MongoConnector(sc).withDatabaseDo(WriteConfig(sc), {db => db.getCollection("avgDayAndCK").drop()})
	
	
	//For compatibility with MongoDB 2.6, don't use MongoDefaultPartitioner nor MongoSamplePartitioner
	val readConfig = ReadConfig(Map("partitioner" -> "MongoPaginateBySizePartitioner"), Some(ReadConfig(sc)))
	
	val rdd = MongoSpark.load(sc, readConfig)
	
	val rddDate = rdd.map(doc => (doc.get("_p").asInstanceOf[org.bson.Document].get("enercons:contract"), LocalDateTime.ofInstant(doc.get("_p").asInstanceOf[org.bson.Document].get("enercons:date").asInstanceOf[Date].toInstant, ZoneId.systemDefault).toLocalDate().toString(), 1, if (doc.get("_p").asInstanceOf[org.bson.Document].getDouble("enercons:globalKWH").getClass.toString() == "class java.lang.Integer") doc.get("_p").asInstanceOf[org.bson.Document].getInteger("enercons:globalKWH").toDouble.asInstanceOf[java.lang.Double] else doc.get("_p").asInstanceOf[org.bson.Document].getDouble("enercons:globalKWH").asInstanceOf[java.lang.Double]))
	
	val rddDateRedByKey = rddDate.map(r => ((r._1, r._2), r)).reduceByKey((a,b) => (a._1, a._2, a._3+b._3, a._4+b._4))
	
	val finalRdd = rddDateRedByKey.map(a => (a._1, a._2._4/a._2._3))
	
	val res = finalRdd.map(t => new Document("globalKWH", t._2).append("contract", t._1._1).append("date", Date.from(LocalDate.parse(t._1._2).atStartOfDay(ZoneId.systemDefault()).toInstant())))

	res.saveToMongoDB()
	
	}
}
