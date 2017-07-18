package org.ozwillo.energy.spark;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.spark.launcher.SparkLauncher;
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
	@Value("${spring.data.mongodb.energyProject}")
	private String energyProject;
	@Value("${spring.data.mongodb.energyContractCollection}")
	private String energyContractCollection;
	@Value("${spring.data.mongodb.energyConsumptionCollection}")
	private String energyConsumptionCollection;
	@Value("${spring.data.mongodb.readPreference}")
	private String readPreference;

	public EnergyAggregationServices() {
		super();
	}

	/**
	 * Executes the runAggregation method on startup if the -DrunAggregation parameter
	 * is given to Maven.
	 * @throws IOException exception eventually thrown by the spark process
	 * @throws InterruptedException exception eventually thrown by the spark process
	 */
    @PostConstruct
	public void init() throws IOException, InterruptedException {
      String runAggregationString = System.getProperty("runAggregation");
      if (runAggregationString != null && !"false".equals(runAggregationString)) {
	      this.runAggregation();
	   }
	}

	/**
	 * Runs all the energy consumption aggregations, be it grouped by city or contract
	 * and time measure.
	 * Scheduled to run at midnight every day every month.
	 * @param city the city for which to run the data aggregation
	 * @param timeMeasure the time measure to use for the groupBy clause
	 * @throws IOException exception eventually thrown by the spark process
	 * @throws InterruptedException exception eventually thrown by the spark process
	 */
	@Scheduled(cron="0 0 1 * * *")
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
		List<String> appArgs = Arrays.asList(
				"--datacore-mongo-IP", datacoreMongoIP,
				"--datacore-mongo-id", datacoreMongoId,
				"--aggregation-mongo-IP", aggregationMongoIP,
				"--aggregation-mongo-id", aggregationMongoId,
				"--energy-project", energyProject,
				"--energy-contract-collection", energyContractCollection,
				"--energy-consumption-collection", energyConsumptionCollection,
				"--read-preference", readPreference,
				"--aggregation-type", "all",
				"--all-cities");

		System.out.println("Command args are : " + appArgs.toString());

		SparkLauncher spark = new SparkLauncher(env)
				//.addSparkArg("--driver-java-options", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=7500")
			    .setSparkHome(sparkHome)
			    .setAppResource(mavenRepository +
			    		"/oz-energy-aggregations/oz-energy-aggregations_2.10/1.0/oz-energy-aggregations_2.10-1.0-assembly.jar")
			    .setMainClass("Aggregations");

		Iterator<String> argsIterator = appArgs.iterator();
		while (argsIterator.hasNext()) {
			spark.addAppArgs(argsIterator.next());
		}

		Process sparkProcess = spark.launch();

		System.out.println("Waiting for aggregation of all data to finish...");
		int exitCode = sparkProcess.waitFor();
		System.out.println(IOUtils.toString(sparkProcess.getErrorStream()));
		System.out.println("Finished! Exit code:" + exitCode);
	}


	/**
	 * Runs the energy consumption average aggregation for a given city grouped by a given time measure.
	 * TODO Schedule it
	 * @param city the city for which to run the data aggregation
	 * @param timeMeasure the time measure to use for the groupBy clause
	 * @throws IOException exception eventually thrown by the spark process
	 * @throws InterruptedException exception eventually thrown by the spark process
	 */
	public void runCityAggregation(String city, String timeMeasure) throws IOException, InterruptedException{
		Map<String, String> env = new HashMap<String,String>();
		env.put("JAVA_HOME", System.getProperty("java.home"));
		List<String> appArgs = Arrays.asList(
				"--datacore-mongo-IP", datacoreMongoIP,
				"--datacore-mongo-id", datacoreMongoId,
				"--aggregation-mongo-IP", aggregationMongoIP,
				"--aggregation-mongo-id", aggregationMongoId,
				"--energy-project", energyProject,
				"--energy-contract-collection", energyContractCollection,
				"--energy-consumption-collection", energyConsumptionCollection,
				"--read-preference", readPreference,
				"--aggregation-type", "avg",
				"--groupBy-time", timeMeasure,
				"--groupBy-otherDimension", "city",
				"--city", city);

		System.out.println("Command args are : " + appArgs.toString());

		SparkLauncher spark = new SparkLauncher(env)
			    .setSparkHome(sparkHome)
			    .setAppResource(mavenRepository +
			    		"/oz-energy-aggregations/oz-energy-aggregations_2.10/1.0/oz-energy-aggregations_2.10-1.0-assembly.jar")
			    .setMainClass("Aggregations");

		Iterator<String> argsIterator = appArgs.iterator();
		while (argsIterator.hasNext()) {
			spark.addAppArgs(argsIterator.next());
		}

		Process sparkProcess = spark.launch();

		System.out.println("Waiting for city aggregation to finish...");
		int exitCode = sparkProcess.waitFor();
		System.out.println(IOUtils.toString(sparkProcess.getErrorStream()));
		System.out.println("Finished! Exit code:" + exitCode);
	}
}
