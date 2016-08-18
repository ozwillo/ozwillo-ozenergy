package org.ozwillo.energy.spark;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ozwillo.energy.OzEnergyApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OzEnergyApplication.class)
public class SparkAggregationTests {

	private EnergyAggregationServices service = new EnergyAggregationServices();
	
	@Test
	public void launchSparkAggregation() throws IOException, InterruptedException {
		service.runAggregation();
	}
}
