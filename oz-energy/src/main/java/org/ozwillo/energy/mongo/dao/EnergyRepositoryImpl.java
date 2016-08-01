package org.ozwillo.energy.mongo.dao;

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

import java.util.List;

public class EnergyRepositoryImpl implements EnergyRepositoryCustom {

	private final MongoTemplate mongoTemplate;
	
	@Autowired
	public EnergyRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<Energy> aggregateByDay(int customerKey) {
		
		MatchOperation match = match(Criteria.where("customerKey").is(customerKey));
		ProjectionOperation project = project()
				.andExpression("dayOfMonth(date)").as("day")
				.andExpression("month(date)").as("month")
				.andExpression("year(date)").as("year")
				.andExpression("consumption").as("conso");
		GroupOperation group = group("year","month","day")
				.avg("conso").as("General_Supply_KWH");
		
		Aggregation aggregation = newAggregation(
				match,
				project,
				group,
				sort(Sort.Direction.ASC, "year", "month", "day")
				);
		List<Energy> e = mongoTemplate.aggregate(aggregation, Energy.class, Energy.class).getMappedResults();
		for(Energy energy:e){
			energy.setDateByYMD();
			energy.setCustomerKey(customerKey);
			energy.setId(customerKey + "/" + energy.getDate().toString());
		}
		return e;
	}

}
