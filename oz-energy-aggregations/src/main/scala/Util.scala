import com.mongodb.spark._

import com.mongodb.spark.MongoConnector
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.JavaConverters._
import org.bson.Document

import java.util.Date
import java.time.ZoneId
import java.time.LocalDate

trait Util {
  def dateFromStringdate(s: String): Date = {
		Date.from(LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()).toInstant())
	}
	
	def cityCollection(s: String): String = {
		"avgDayAndCKFor" + s
	}
	
	def writeConfigCity(s: String): WriteConfig = {
		val outputUri = "mongodb://127.0.0.1/datacore1.avgDayAndCKFor" + s
		WriteConfig(Map("uri" -> outputUri))
	}
}