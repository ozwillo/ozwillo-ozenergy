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

public class EnergyAggregationServices {

	@Value("/home/charge/.m2/repository/spark_aggregations")
	private String mavenRepository;
	
	public EnergyAggregationServices() {
		super();
		this.mavenRepository = System.getProperty("user.home")+"/.m2/repository/spark_aggregations";
	}
	
	@Autowired
	@PostConstruct
	@Scheduled(cron="0 0 0 1/1 * ? *") //Runs at midnight every day every month
	public void runAggregation() throws IOException, InterruptedException{
		Map env = new HashMap<String,String>();
		env.put("JAVA_HOME", System.getProperty("java.home"));
		Process spark = new SparkLauncher(env)
				/* Normally, you should set JAVA_HOME the following way, but it doesn't work 
				 * See http://apache-spark-developers-list.1001551.n3.nabble.com/
				 * SparkLauncher-setJavaHome-does-not-set-JAVA-HOME-in-child-process-td14848.html
				 * 
				 * Solved by setting environment
				 */
				//.setJavaHome("/home/charge/installations/jdk1.8.0_91")
			    .setSparkHome("/home/charge/installations/spark-1.6.1-bin-hadoop2.6")
			    .setAppResource(mavenRepository + 
			    		"/oz-energy-aggregations/oz-energy-aggregations_2.10/1.0/oz-energy-aggregations_2.10-1.0-assembly.jar")
			    .setMainClass("Aggregations").addAppArgs("all").launch();

		System.out.println("Waiting for finish...");
		int exitCode = spark.waitFor();
		System.out.println(IOUtils.toString(spark.getErrorStream()));
		System.out.println("Finished! Exit code:" + exitCode);
	}
}
