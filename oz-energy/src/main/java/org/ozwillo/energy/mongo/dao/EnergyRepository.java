package org.ozwillo.energy.mongo.dao;

import java.util.List;

import org.ozwillo.energy.core.mongo.model.Energy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EnergyRepository extends MongoRepository<Energy, String>, EnergyRepositoryCustom {

	public List<Energy> findByCustomerKey(int customerKey);
	public Energy findFirstByOrderByIdAsc();

}
