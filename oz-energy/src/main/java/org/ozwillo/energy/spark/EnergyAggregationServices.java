package org.ozwillo.energy.spark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EnergyAggregationServices {

	@Value("${HOME}/.m2/repository/spark_aggregations")
	private String mavenRepository;
	@Value("${spark.home}")
	private String sparkHome;
	
	@Value("${datacore.data.mongodb.host}")
	private String datacoreMongoIP;
	@Value("${datacore.data.mongodb.database}")
	private String datacoreMongoId;
	@Value("${spring.data.mongodb.host}")
	private String aggregationMongoIP;
	@Value("${spring.data.mongodb.database}")
	private String aggregationMongoId;

	public EnergyAggregationServices() {
		super();
	}

    @PostConstruct
	public void init() throws IOException, InterruptedException {
      String runAggregationString = System.getProperty("runAggregation");
      if (runAggregationString != null && !"false".equals(runAggregationString)) {
	      this.runAggregation();
	   }
	}

	@Scheduled(cron="0 0 1 * * *") // Runs at midnight every day every month
	public void runAggregation() throws IOException, InterruptedException{
		Map<String,String> env = new HashMap<String,String>();
		env.put("JAVA_HOME", System.getProperty("java.home"));
		/* Normally, you should set JAVA_HOME with
		 * ".setJavaHome("/home/charge/installations/jdk1.8.0_91")", but it
		 * doesn't work .
		 * See http://apache-spark-developers-list.1001551.n3.nabble.com/
		 * SparkLauncher-setJavaHome-does-not-set-JAVA-HOME-in-child-process-td14848.html
		 *
		 * Solved by setting environment
		 */
		String appArgs = "--datacore-mongo-IP"
				+ " " + datacoreMongoIP + " "
				+ "--datacore-mongo-id"
				+ " " + datacoreMongoId + " "
				+ "--aggregation-mongo-IP"
				+ " " + aggregationMongoIP + " "
				+ "--aggregation-mongo-id"
				+ " " + aggregationMongoId + " "
				+ "--aggregation-type"
				+ " " + "all" + " "
				+ "--all-cities";
		System.out.println("Command args are : " + appArgs);
		
		Process spark = new SparkLauncher(env)
			    .setSparkHome(sparkHome)
			    .setAppResource(mavenRepository +
			    		"/oz-energy-aggregations/oz-energy-aggregations_2.10/1.0/oz-energy-aggregations_2.10-1.0-assembly.jar")
			    .setMainClass("Aggregations")
				.addAppArgs(appArgs)
				.launch();
		
		System.out.println("Waiting for aggregation of all data to finish...");
		int exitCode = spark.waitFor();
		System.out.println(IOUtils.toString(spark.getErrorStream()));
		System.out.println("Finished! Exit code:" + exitCode);
	}


	/**
	 * NOT USED YET
	 * TODO finish it, and schedule it
	 * @param city
	 * @param timeMeasure
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void runCityAggregation(String city, String timeMeasure) throws IOException, InterruptedException{
		Map env = new HashMap<String,String>();
		env.put("JAVA_HOME", System.getProperty("java.home"));
		String appArgs = "--datacore-mongo-IP"
				+ " " + datacoreMongoIP + " "
				+ "--datacore-mongo-id"
				+ " " + datacoreMongoId + " "
				+ "--aggregation-mongo-IP"
				+ " " + aggregationMongoIP + " "
				+ "--aggregation-mongo-id"
				+ " " + aggregationMongoId + " "
				+ "--aggregation-type"
				+ " " + "avg" + " "
				+ "--groupBy-time"
				+ " " + timeMeasure + " "
				+ "--groupBy-otherDimension"
				+ " " + "city" + " "
				+ "--city"
				+ " " + city;
		System.out.println("Command args are : " + appArgs);
		
		Process spark = new SparkLauncher(env)
			    .setSparkHome(sparkHome)
			    .setAppResource(mavenRepository +
			    		"/oz-energy-aggregations/oz-energy-aggregations_2.10/1.0/oz-energy-aggregations_2.10-1.0-assembly.jar")
			    .setMainClass("Aggregations")
			    .addAppArgs(appArgs)
			    .launch();

		System.out.println("Waiting for city aggregation to finish...");
		int exitCode = spark.waitFor();
		System.out.println(IOUtils.toString(spark.getErrorStream()));
		System.out.println("Finished! Exit code:" + exitCode);
	}


}
