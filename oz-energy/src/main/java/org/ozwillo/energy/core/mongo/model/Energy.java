package org.ozwillo.energy.core.mongo.model;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Energy {
	
	@Id
	private String id;
	
	@Field("contract")
	@JsonProperty
	private String contract;
	
	@Field("date")
	@JsonProperty
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private DateTime date;
	
	@Field("globalKW")
	@JsonProperty
	private double consumption;
	
	private int year;
	private int month;
	private int day;
	
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

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

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

}
