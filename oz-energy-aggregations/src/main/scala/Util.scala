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
import java.time.LocalDateTime


trait Util {
  /** Creates a new date from a String "YYYY-MM-DD"
   * 
   * @param s the date in String format "YYYY-MM-DD"
   */
  def dateFromStringdate(s: String): Date = {
		Date.from(LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()).toInstant())
	}
	
  /** Creates the date "YYYY-MM-02T00:00:00" from the date "YYYY-MM-DDThh:mm:ss"
   *  
   *  @param date a date
   *  @return a new date : the first day of the month and year from the previous date
   */
  def dateFromMonthFromDate(date: Date): Date =  {
    val s = LocalDateTime.ofInstant(date.asInstanceOf[Date].toInstant, ZoneId.systemDefault)
      .toLocalDate().toString().substring(0,7)
    dateFromStringdate(s+"-02")
  }
  
  /** Creates the date "YYYY-01-02T00:00:00" from the date "YYYY-MM-DDThh:mm:ss"
   * 
   * @param date 
   * @return a new date : the first day of the first month of the year given
   */
  def dateFromYearFromDate(date: Date): Date = {
    val s = LocalDateTime.ofInstant(date.asInstanceOf[Date].toInstant, ZoneId.systemDefault)
      .toLocalDate().toString().substring(0,4)
    dateFromStringdate(s+"-01-02") 
  }
  
  /** Creates the name of a city collection from its name
   * 
   * @param s the city name
   * @param agg the aggregation's name
   * @return the city collection
   */
	def cityCollection(s: String, agg: String): String = {
		agg + s
	}
	
	/** Creates the configuration to write in the MongoDB collection corresponding to a city
	 * 
	 * @param s the city's name
	 * @param agg the aggregation's name
	 * @return the MongoDB's write configuration
	 */
	def writeConfigCity(s: String, agg:String): WriteConfig = {
		val outputUri = "mongodb://127.0.0.1/datacore1."+ agg + s
		WriteConfig(Map("uri" -> outputUri))
	}
}