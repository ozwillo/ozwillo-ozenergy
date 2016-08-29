package org.ozwillo.energy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import org.ozwillo.energy.core.mongo.model.Energy;
import org.ozwillo.energy.mongo.dao.EnergyRepository;
import org.ozwillo.energy.spark.EnergyAggregationServices;

@RestController
@RequestMapping("/api/my/conso")
public class AjaxServices {
	
	@Autowired
	private EnergyRepository repository;
	
	private EnergyAggregationServices sparkService = new EnergyAggregationServices();
	
	@RequestMapping(value="/contract/avg/day", method = RequestMethod.GET)
	public List<Energy> avgConsumerDataByDay() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "avgDayAndContract");
		return energy;
	}

	@RequestMapping(value="/contract/avg/month", method = RequestMethod.GET)
	public List<Energy> avgConsumerDataByMonth() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "avgMonthAndContract");
		return energy;
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
		return energy;
	}
	
	@RequestMapping(value="/contract/sum/month", method = RequestMethod.GET)
	public List<Energy> sumConsumerDataByMonth() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "sumMonthAndContract");
		return energy;
	}
	
	@RequestMapping(value="/contract/sum/year", method = RequestMethod.GET)
	public List<Energy> sumConsumerDataByYear() {
		String contract = "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy= repository.findByContract(contract, "sumYearAndContract");
		return energy;
	}
	
	@RequestMapping(value="/city/{city}/day", method = RequestMethod.GET)
	public List<Energy> findByCityAndDay(@PathVariable String city) throws IOException, InterruptedException{
		List<Energy> energy= repository.findByCity(city, "avgDay");
		if (energy.isEmpty()) {
			sparkService.runCityAggregation(city, "day");
			energy= repository.findByCity(city, "avgDay");
		}
		return energy;
	}

	@RequestMapping(value="/city/{city}/month", method = RequestMethod.GET)
	public List<Energy> findByCityAndMonth(@PathVariable String city) throws IOException, InterruptedException{
		List<Energy> energy= repository.findByCity(city, "avgMonth");
		if (energy.isEmpty()) {
			sparkService.runCityAggregation(city, "month");
			energy= repository.findByCity(city, "avgMonth");
		}
		return energy;
	}
	
	@RequestMapping(value="/city/{city}/year", method = RequestMethod.GET)
	public List<Energy> findByCityAndYear(@PathVariable String city) throws IOException, InterruptedException{
		List<Energy> energy= repository.findByCity(city, "avgYear");
		if (energy.isEmpty()) {
			sparkService.runCityAggregation(city, "year");
			energy= repository.findByCity(city, "avgYear");
		}
		return energy;
	}
	
}
