package org.ozwillo.energy.mongo.dao;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.ozwillo.energy.core.mongo.model.Energy;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;



public class EnergyRepositoryImpl implements EnergyRepositoryCustom {

	private final MongoTemplate mongoTemplate;
	
	@Autowired
	public EnergyRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<Energy> aggregateByDay(int customerKey) {
//		Aggregation aggregation = newAggregation(
//				match(Criteria.where("CUSTOMER_KEY").is(customerKey)),
//				project()
//					.andExpression("dayOfMonth(Date)").as("day")
//					.andExpression("month(Date)").as("month")
//					.andExpression("year(Date)").as("year")
//					.andExpression("new ISODate(year,month,day)").as("tDate"),
//				group(fields().and("CUSTOMER_KEY").and("tDate","Date"))
//					.avg("General_Supply_KWH").as("General_Supply_KWH"),
//				sort(Sort.Direction.ASC, "tDate")
//				);
		MatchOperation match = match(Criteria.where("customerKey").is(customerKey));
		ProjectionOperation project = project()
				.andExpression("dayOfMonth(date)").as("day")
				.andExpression("month(date)").as("month")
				.andExpression("year(date)").as("year")
				//.andExpression("DateTime(year(date),month(date),dayOfMonth(date),0,0)").as("tDate")
				.andExpression("consumption").as("conso");
		GroupOperation group = group("year","month","day")
				.avg("conso").as("General_Supply_KWH");
		ProjectionOperation project2 = project("General_Supply_KWH","year","month","day");
				//.andExpression("(new DateTime('year','month','day',0))").as("Date");
				//.andExpression("year").as("Date");
				
		
		
		Aggregation aggregation = newAggregation(
				match,
				project,
				group,
				project2,
				sort(Sort.Direction.ASC, "year", "month", "day")
				);
		List<Energy> e = mongoTemplate.aggregate(aggregation, Energy.class, Energy.class).getMappedResults();
		for(Energy energy:e){
			energy.setDateByYMD();
			energy.setId(customerKey + "/" + energy.getDate().toString());
		}
		return e;
	}

}
