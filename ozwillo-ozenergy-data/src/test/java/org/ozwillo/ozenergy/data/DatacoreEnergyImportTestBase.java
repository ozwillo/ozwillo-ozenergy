package org.ozwillo.ozenergy.data;

import static org.ozwillo.ozenergy.data.EnergyMapperHelper.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis.datacore.rest.api.DCResource;
import org.oasis.datacore.rest.api.DatacoreApi;
import org.oasis.datacore.rest.api.util.DCURI;
import org.oasis.datacore.rest.client.DatacoreCachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Injects energy data :
 * TODO
 *
 * Requires model to have been already imported (ex. using the Playground Import tool)
 *
 * @author mdutoo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
///@ContextConfiguration(locations = { "classpath:oasis-datacore-ozenergy-data-context.xml" }) // NOT classpath:oasis-datacore-rest-server-test-context.xml
//@FixMethodOrder(MethodSorters.NAME_ASCENDING) // else random since java 7 NOT REQUIRED ANYMORE
public abstract class DatacoreEnergyImportTestBase {
	
	private static final Logger logger = LoggerFactory.getLogger(CsvResourceBulkImportService.class);
	
   /** inited in inheriting classes */
   protected DatacoreApi datacoreApi;

   @Autowired
   @Qualifier("datacoreApiCachedJsonClient")
   protected DatacoreCachedClient datacoreApiClient;

   /** to clean cache for tests */
   @Autowired
   @Qualifier("datacore.rest.client.cache.rest.api.DCResource")
   private Cache resourceCache; // EhCache getNativeCache

   /** to be able to build a full uri, to check in tests
    * TODO rather client-side DCURI or rewrite uri in server */
   ///@Value("${datacoreApiClient.baseUrl}")
   ///private String baseUrl; // useless
   @Value("${datacoreApiClient.containerUrl}")
   private String containerUrlString;
   @Value("#{new java.net.URI('${datacoreApiClient.containerUrl}')}")
   //@Value("#{uriService.getContainerUrl()}")
   protected URI containerUrl;

   @Test
   public void importEnergySampleData() throws Exception {
      // ASSUMING there are already resources (and their models) :
      // geo_1 (with addrpostci:name), org_1 (with providers)
      // as well as models of : org_1.persid:, energy_0.ener*
	   
      CsvResourceBulkImportService csvImportService = new CsvResourceBulkImportService();
      csvImportService.setDatacoreApi(datacoreApi);

      // providers :
      //String csvResourcePath = "energy/energy_providers.csv";
      ///final String defaultProviderId = "FR/49015839100014";

      // consumers (persid) :
      // TODO LATER login as each consumer user (provided in csv) and batch = 1 to setup permissions
      csvImportService.importCsv("energy/energy_consumers.csv", 1000000, true,
            "org_1", line -> {
         String countryId = line[12]; // geocofr:idIso
         String cityId = countryId + '/' + line[14] + '/' + line[15]; // geon3fr:idIso  geocifr:name
         String consumerId = hashCodeId(line[4]);
         DCResource r = null;
			try {
				r = DCResource.create(null, "persid:Identity_0")
				       .set("persid:firstName", line[0])
				       .set("persid:lastName", line[1])
				       .set("persid:displayName", line[2])
				       //.set("persid:gender", line[3])
				       .set("persid:email", line[4])
				       .set("persid:id", consumerId)
				       //.set("persid:birthDate", DateTime.parse(line[5])) // joda rather than java 8 date (not supported locally)
				       .set("adrpost:fullAddress", line[6])
				       .set("adrpost:streetAndNumber", line[7])
				       //.set("adrpost:supField", line[8])
				       .set("adrpost:postCode", line[9])
				       //.set("adrpost:POBox", line[10])
				       //.set("adrpost:cedex", line[11])
				       .set("adrpost:postName", (new DCURI(containerUrl, "geocifr:Commune_0", cityId)).toURI().toString())
				       .set("adrpost:country", (new DCURI(containerUrl, "geocofr:Pays_0", countryId)).toURI().toString());
			} catch (URISyntaxException e) {
				logger.error("Couldn't build postName/country URI" + e);
			}
         // computed props : NOO in spreadsheet macro
         /*r.set("persid:displayName", "" + r.get("persid:firstName") + ' ' + r.get("persid:lastName"));
         r.set("persid:fullAddress", "" + r.get("persid:firstName") + ' ' + r.get("persid:lastName"));*/
         // once props are complete, build URI out of them :
         r.setUriFromId(containerUrl, consumerId);

         return new DCResource[] { r };
      });

      // contracts (enercontr, from persid csv) :
      // TODO LATER login as each consumer user (provided in csv) and batch = 1 to setup permissions
      final Map<String,String> customerKeyToContractId = new HashMap<String,String>();
      //importCsv("energy/energy_consumers.csv", 1000000, true,
      csvImportService.importCsv("energy/energy_contracts.csv", 1000000, true,
            null, line -> {
         // energy_contracts.csv fields :
         // orgfr:siret geocofr:idIso  persid:email   enercontr:displayName   enercontr:customerKey
         String providerId = line[1] + '/' + line[0];
         String consumerId = hashCodeId(line[2]);
         String contractId = providerId + '/' + consumerId;
         String customerKey = line[4];
         customerKeyToContractId.put(customerKey, contractId);

         // OR rather generating it from energy_consumers.csv :
         ///String providerId = defaultProviderId;
         //String consumerId = hashCodeId(line[4]);
         DCResource cr = null;
		try {
			cr = DCResource.create(null, "enercontr:EnergyConsumptionContract_0")
			       .set("enercontr:provider", (new DCURI(containerUrl, "orgfr:Organisation_0", providerId)).toURI().toString())
			       .set("enercontr:consumer", (new DCURI(containerUrl, "persid:Identity_0", consumerId)).toURI().toString())
			       .set("odisp:displayName", line[3])
			       .set("enercontr:customerKey", customerKey);
		} catch (URISyntaxException e) {
			logger.error("Couldn't build provider/consumer URI" + e);
		}
         cr.setUriFromId(containerUrl, contractId);

         return new DCResource[] { cr };
      });

      //String csvResourcePath = "energy/energy_contracts.csv";

      //String consumptionsCsvPath = "/home/mdutoo/dev/oasis/workspace/ozwillo-ozenergy/energy-ok-import-datacore.csv";
      String consumptionsCsvPath = "energy/energy-ok-import-datacore.csv";
      final List<String> customerKeysWithoutContract = new ArrayList<String>();
      csvImportService.importCsv(consumptionsCsvPath, 1000000, false,
            null, line -> {
         String customerKey = line[0];
         String contractId = customerKeyToContractId.get(customerKey); // "FR/49015839100014/964549036";
         if (contractId == null) {
            customerKeysWithoutContract.add(customerKey);
            return null;
         }
         DCResource r = null;
		try {
			r = DCResource.create(null, "enercons:EnergyConsumption_0")
			       .set("enercons:contract", (new DCURI(containerUrl, "enercontr:EnergyConsumptionContract_0", contractId)).toURI().toString())
			       .set("enercons:date", DateTime.parse(line[1])) // joda rather than java 8 date (not supported locally)
			       .set("enercons:globalKWH", parseNumber(line[7]));
		} catch (URISyntaxException e) {
			logger.error("Couldn't build contract URI" + e);
		}

         r.setUriFromId(containerUrl, contractId + '/' + r.get("enercons:date"));
         return new DCResource[] { r };
      });

      System.out.println("Not imported " + customerKeysWithoutContract.size()
         + " consumptions having no contract with the following customerKeys: "
         + new HashSet<String>(customerKeysWithoutContract));
   }


}
