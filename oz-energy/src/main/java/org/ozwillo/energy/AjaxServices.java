package org.ozwillo.energy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.ozwillo.energy.core.mongo.model.Energy;
import org.ozwillo.energy.mongo.dao.EnergyRepository;

@RestController
@RequestMapping("/api/my/conso")
public class AjaxServices {
	
	@Autowired
	private EnergyRepository repository;

	@RequestMapping(value="/{ck}", method = RequestMethod.GET)
	public List<Energy> findConsumerData(@PathVariable int ck) {
		List<Energy> energy = repository.findByCustomerKey(ck);
		List<Energy> tenFirst = energy.subList(0, 10);
 		return tenFirst;
	}
	
	@RequestMapping(value="/{ck}/day", method = RequestMethod.GET)
	public List<Energy> aggregateConsumerDataByDay(@PathVariable int ck) {
		List<Energy> energy= repository.aggregateByDay(ck);
		List <Energy> tenFirst = energy.subList(0, 10);
		return tenFirst;
	}

}
