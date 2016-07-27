package org.ozwillo.energy.mongo.dao;

import java.util.List;

import org.ozwillo.energy.core.mongo.model.Energy;

public interface EnergyRepositoryCustom {
	
	public List<Energy> aggregateByDay(int customerKey);

}
