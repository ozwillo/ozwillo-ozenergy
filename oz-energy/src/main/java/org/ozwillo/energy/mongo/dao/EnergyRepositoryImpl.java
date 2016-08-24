package org.ozwillo.energy.mongo.dao;

import org.ozwillo.energy.core.mongo.model.Energy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class EnergyRepositoryImpl implements EnergyRepositoryCustom {

	private final MongoTemplate mongoTemplate;
	
	@Autowired
	public EnergyRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<Energy> findByContract(String contract, String collectionName) {
		Query query = new Query().addCriteria(Criteria.where("contract").is(contract))
				.with(new Sort(new Order(Direction.ASC, "date")));
		return mongoTemplate.find(query, Energy.class, collectionName);
	}
	
	@Override
	public List<Energy> findByCity(String city, String aggregation) {
		String collectionName = aggregation + "For" + city;
		Query query = new Query().with(new Sort(new Order(Direction.ASC, "date")));
		return mongoTemplate.find(query, Energy.class, collectionName);
	}

}
