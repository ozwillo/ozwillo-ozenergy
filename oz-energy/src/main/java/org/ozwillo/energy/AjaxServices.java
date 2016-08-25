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
	
	@RequestMapping(value="/contract/avg/day", method = RequestMethod.GET)
	public List<Energy> avgConsumerDataByDay() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "avgDayAndContract");
		List <Energy> tenFirst = energy.subList(0, 10);
		return tenFirst;
	}

	@RequestMapping(value="/contract/avg/month", method = RequestMethod.GET)
	public List<Energy> avgConsumerDataByMonth() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "avgMonthAndContract");
		List <Energy> tenFirst = energy.subList(0, 10);
		return tenFirst;
	}
	
	@RequestMapping(value="/contract/avg/year", method = RequestMethod.GET)
	public List<Energy> avgConsumerDataByYear() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "avgYearAndContract");
		return energy;
	}
	
	@RequestMapping(value="/contract/sum/day", method = RequestMethod.GET)
	public List<Energy> sumConsumerDataByDay() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "sumDayAndContract");
		List <Energy> tenFirst = energy.subList(0, 10);
		return tenFirst;
	}
	
	@RequestMapping(value="/contract/sum/month", method = RequestMethod.GET)
	public List<Energy> sumConsumerDataByMonth() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "sumMonthAndContract");
		List <Energy> tenFirst = energy.subList(0, 10);
		return tenFirst;
	}
	
	@RequestMapping(value="/contract/sum/year", method = RequestMethod.GET)
	public List<Energy> sumConsumerDataByYear() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "sumYearAndContract");
		return energy;
	}

	
}
