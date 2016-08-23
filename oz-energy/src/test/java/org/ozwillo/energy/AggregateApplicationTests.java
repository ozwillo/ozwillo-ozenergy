package org.ozwillo.energy;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ozwillo.energy.core.mongo.model.Energy;
import org.ozwillo.energy.mongo.dao.EnergyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OzEnergyApplication.class)
public class AggregateApplicationTests {
	
	@Autowired
	private EnergyRepository repository;

	@Test
	public void contextLoads() {
	}
	
	public void displayResults(String collectionName, List<Energy> energy, String unit) {
		System.out.println("-----------------------------------------------------");
		System.out.println("Total number of data for " + collectionName + " : " + energy.size());
		System.out.println("Extract (10 first) :");
		List <Energy> tenFirst = energy.subList(0, 10);
		for(Energy en: tenFirst) {
			System.out.println(en.getContract() + " - " + en.getDate() + " : " + en.getConsumption() + "(" + unit +")");
		}
		System.out.println("-----------------------------------------------------");
	}
	
	@Test
	public void findByContractTest() {
		String contract = "http://data.ozwillo.com/dc/type/"
				+ "enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
		List<Energy> energy = repository.findByContract(contract, "avgDayAndCK");
		displayResults("avgDayAndCK", energy, "kWh");
		
	}
	
	@Test
	public void findByCityTest() {
		List<Energy> energy = repository.findByCity("Lyon", "avgDay");
		displayResults("avgDayForLyon", energy, "kWh");
	}

}
