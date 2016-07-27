package org.ozwillo.energy.core.mongo.model;

import org.joda.time.DateTime;

//import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;



@Document(collection = "energy")
public class Energy {

	@Id
	private String id;
	
	@Field("CUSTOMER_KEY")
	@JsonProperty
	private int customerKey;
	
	@Field("Date")
	@JsonProperty
	@DateTimeFormat(iso=ISO.DATE_TIME)
	//private Date date;
	private DateTime date;
	
	@Field("General_Supply_KWH")
	@JsonProperty
	private double consumption;
	
	private int year;
	private int month;
	private int day;
	
	
//	private List<String> modelType;
//
//	@JsonProperty
//	private List<String> ancestors;
//
//	private List<String> nameTokens;
//
//	@JsonProperty
//	private String country;
//
//	/** URI in Datacore  */
//	@JsonProperty
//	private String uri;

	public void setDateByYMD(){
		this.date = new DateTime(this.year, this.month, this.day, 0, 0);
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCustomerKey() {
		return customerKey;
	}

	public void setCustomerKey(int customerKey) {
		this.customerKey = customerKey;
	}

//	public Date getDate() {
//		return date;
//	}
//
//	public void setDate(Date date) {
//		this.date = date;
//	}
	
	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public double getConsumption() {
		return consumption;
	}

	public void setConsumption(double consumption) {
		this.consumption = consumption;
	}

//	public List<String> getModelType() {
//		return modelType;
//	}
//
//	public void setModelType(List<String> modelType) {
//		this.modelType = modelType;
//	}
//
//	public List<String> getAncestors() {
//		return ancestors;
//	}
//
//	public void setAncestors(List<String> ancestors) {
//		this.ancestors = ancestors;
//	}
//
//	public List<String> getNameTokens() {
//		return nameTokens;
//	}
//
//	public void setNameTokens(List<String> nameTokens) {
//		this.nameTokens = nameTokens;
//	}
//
//	public String getCountry() {
//		return country;
//	}
//
//	public void setCountry(String country) {
//		this.country = country;
//	}
//
//	public String getUri() {
//		return uri;
//	}
//
//	public void setUri(String uri) {
//		this.uri = uri;
//	}
	
	
	
}
