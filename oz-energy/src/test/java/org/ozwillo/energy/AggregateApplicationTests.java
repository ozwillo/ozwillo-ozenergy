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
	
	@Test
	public void aggregateByDayTest(){
		int ck = 8170837;
		List<Energy> energy= repository.aggregateByDay(ck);
		List <Energy> tenFirst = energy.subList(0, 10);
		for(Energy en : tenFirst){
			System.out.println(en.getId() + " - "+ en.getDate() + " - " + en.getConsumption());
		}
		
	}

}
