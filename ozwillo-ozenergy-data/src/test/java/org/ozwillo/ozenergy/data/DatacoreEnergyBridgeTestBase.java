package org.ozwillo.ozenergy.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis.datacore.rest.api.DCResource;
import org.oasis.datacore.rest.api.DatacoreApi;
import org.oasis.datacore.rest.api.util.DCURI;
import org.oasis.datacore.rest.api.util.UriHelper;
import org.oasis.datacore.rest.client.DatacoreCachedClient;
import org.oasis.datacore.rest.client.QueryParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Injects energy data from Blynk server PostgreSQL DB.
 *
 * Requires model to have been already imported (ex. using the Playground Import tool).
 *
 *
 * @author brenault
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class DatacoreEnergyBridgeTestBase {
	// DB credentials, ok to leave them this way since DB is not accessible from the outside
	private static final String DB_URL ="jdbc:postgresql://localhost:5432/blynk";
	private static final String DB_USERNAME = "test";
	private static final String DB_PASSWORD = "test";
	
	// Duration to wait between importations to Datacore
	private static final int SLEEP_TIME = 5000;
	
	private static final Logger logger = LoggerFactory.getLogger(DatacoreEnergyBridgeTestBase.class);
	
   /** inited in inheriting classes */
   protected DatacoreApi datacoreApi;

   @Autowired
   @Qualifier("datacoreApiCachedJsonClient")
   protected DatacoreCachedClient datacoreApiClient;

   /** to clean cache for tests */
   @Autowired
   @Qualifier("datacore.rest.client.cache.rest.api.DCResource")
   private Cache resourceCache; // EhCache getNativeCache

   /** to be able to build a full uri, to check in tests */
   @Value("${datacoreApiClient.containerUrl}")
   private String containerUrlString;
   @Value("#{new java.net.URI('${datacoreApiClient.containerUrl}')}")
   protected URI containerUrl;

   @Test
   public void importEnergySampleDataTest() {
	    try {
	        while (true) {
	        	try {
					importEnergySampleData();
				} catch (Exception e) {
					logger.error("Importation from DB failed" + e);
				}
	            logger.info("Importation from DB was a success : sleeping for ");
	            Thread.sleep(SLEEP_TIME);
	        }
	    } catch (InterruptedException e) {
	    	logger.error("Sleep failed" + e);
	    }
   }
   
   /**
    * Calls the SQLResourceBulkImportService, providing with a function to match the schema from the DB
    * to the Datacore's one.
    * @throws Exception if anything goes wrong, throws an exception
    */
   public void importEnergySampleData() throws Exception {
	      // ASSUMING there are already resources (and their models) :
	      // geo_1 (with addrpostci:name), org_1 (with providers)
	      // as well as models of : org_1.persid:, energy_0.ener*
		   
		   
		   SQLResourceBulkImportService dbBridgeService = new SQLResourceBulkImportService();
		   dbBridgeService.setDatacoreApi(datacoreApi);
		   

		   DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

	      final List<String> customerKeysWithoutContract = new ArrayList<String>();
	      dbBridgeService.bridgeDB(DB_URL, DB_USERNAME, DB_PASSWORD, 1000000, true,
	            null, line -> {
	         
	         // If DB line doesnt' contain energy consumption data, return null so that it is ignored
	         if (!(line[6].contains("CUSTOMER_KEY") && line[6].contains("CONSUMPTION"))) {
	        	 return null;
	         }
	        
	         // Extract data from payload
	        Pattern customerKeyPattern = Pattern.compile("CUSTOMER_KEY=(.*?);");
			Pattern consumptionPattern = Pattern.compile("CONSUMPTION=(.*?);");
			Matcher customerKeyMatcher = customerKeyPattern.matcher(line[6]);
			Matcher consumptionMatcher = consumptionPattern.matcher(line[6]);
			String customerKey = "";
			while (customerKeyMatcher.find()) {
				customerKey = customerKeyMatcher.group(1);
			}
			String consumption = "";
			while (consumptionMatcher.find()) {
				consumption = consumptionMatcher.group(1);
			}
			
	         // Find the contract associated with the consumer
	         List<DCResource> foundContracts = datacoreApiClient.findDataInType(
	        		 "enercontr:EnergyConsumptionContract_0"
	        		 , new QueryParameters().add("enercontr:customerKey", customerKey)
	        		 , 0
	        		 , 1);
	         if (foundContracts == null || foundContracts.get(0).getUri() == null) {
	             customerKeysWithoutContract.add(customerKey);
	             return null;
	         }
	         String contractId = null;
			try {
				contractId = UriHelper.parseUri(foundContracts.get(0).getUri()).getId();
			} catch (Exception e) {
				logger.error("Couldn't get ID from URI" + e);
			}
	         
			// Create the Datacore resource object but don't post it yet
	         DCResource r = null;
			try {
				r = DCResource.create(null, "enercons:EnergyConsumption_0")
				       .set("enercons:contract", (new DCURI(containerUrl, "enercontr:EnergyConsumptionContract_0", contractId)).toURI().toString())
				       .set("enercons:date", fmt.parseDateTime(line[5])) // joda rather than java 8 date (not supported locally)
				       .set("enercons:globalKWH", consumption);
			} catch (URISyntaxException e) {
				logger.error("Couldn't build contract URI" + e);
			}

	         r.setUriFromId(containerUrl, contractId + '/' + r.get("enercons:date"));
	         
	         return new DCResource[] { r };
	      });

	      logger.info("Not imported " + customerKeysWithoutContract.size()
	         + " consumptions having no contract with the following customerKeys: "
	         + new HashSet<String>(customerKeysWithoutContract));
   }
}
