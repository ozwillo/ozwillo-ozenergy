package org.ozwillo.energy.mongo.dao;

import java.util.List;

import org.ozwillo.energy.core.mongo.model.Energy;

public interface EnergyRepositoryCustom {
	
	public List<Energy> findByContract(String contract, String collectionName);
	public List<Energy> findByCity(String city, String aggregation);

}
