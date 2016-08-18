package org.ozwillo.ozenergy.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oasis.datacore.common.context.SimpleRequestContextProvider;
import org.oasis.datacore.core.entity.query.ldp.LdpEntityQueryServiceImpl;
import org.oasis.datacore.core.meta.DataModelServiceImpl;
import org.oasis.datacore.core.meta.pov.DCProject;
import org.oasis.datacore.core.security.mock.LocalAuthenticationService;
import org.oasis.datacore.rest.api.DCResource;
import org.oasis.datacore.rest.api.DatacoreApi;
import org.oasis.datacore.rest.api.util.UnitTestHelper;
import org.oasis.datacore.rest.client.DatacoreCachedClient;
import org.oasis.datacore.rest.client.cxf.mock.AuthenticationHelper;
import org.oasis.datacore.rest.server.DatacoreApiImpl;
import org.oasis.datacore.rest.server.resource.ResourceService;
import org.oasis.datacore.server.uri.UriService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableMap;

import au.com.bytecode.opencsv.CSVReader;


/**
 * Injects enercontr data
 * 
 * @author mdutoo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:oasis-datacore-ozenergy-data-context.xml" }) // NOT classpath:oasis-datacore-rest-server-test-context.xml
//@FixMethodOrder(MethodSorters.NAME_ASCENDING) // else random since java 7 NOT REQUIRED ANYMORE
public class DatacoreEnergyTest {
   
   @Autowired
   @Qualifier("datacoreApiCachedJsonClient")
   private /*DatacoreApi*/DatacoreCachedClient datacoreApiClient;
   @Autowired
   private ResourceService resourceService;
   
   /** to init models */
   @Autowired
   private /*static */DataModelServiceImpl modelServiceImpl;
   ///@Autowired
   ///private CityCountrySample cityCountrySample;
   
   /** to cleanup db
    * TODO LATER rather in service */
   @Autowired
   private /*static */MongoOperations mgo;

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
   private URI containerUrl;

   /** for testing purpose */
   @Autowired
   @Qualifier("datacoreApiImpl") 
   private DatacoreApiImpl datacoreApiImpl;
   @Autowired
   private LocalAuthenticationService authenticationService;
   /** for testing purpose */
   @Autowired
   private LdpEntityQueryServiceImpl ldpEntityQueryServiceImpl;
   
   
   @Before
   public void reset() {
      
   }
   
   
   public interface IndexedFieldSetMapper<T> {
      public List<T> map(String[] indexedFieldSet);
   } 


   @Test
   public void importEnergySampleData() throws Exception {
      // ASSUMING there are already resources (and their models) :
      // geo_1 (with addrpostci:name), org_1 (with providers)
      // as well as models of : org_1.persid:, oasis.sandbox.ener*
      
      boolean serverRatherThanClient = false;
      
      DatacoreApi datacoreApi;
      if (serverRatherThanClient) {
         authenticationService.loginAs("admin"); // works only on server-side (on client side, also requires Bearer/Basic credentials)  
         datacoreApi = datacoreApiImpl;
      } else {
         AuthenticationHelper.loginBasicAsAdmin(); // works only on client side in devmode 
         datacoreApi = datacoreApiClient;
      }
      /*importCsv(new IndexedFieldSetMapper<DCResource>() {
         public List<DCResource> map(String[] indexedFieldSet) {
            return null;
         }
      });*/

      // providers :
      //String csvResourcePath = "energy/energy_providers.csv";
      ///final String defaultProviderId = "FR/49015839100014";
      
      // consumers (persid) :
      // TODO LATER login as each consumer user (provided in csv) and batch = 1 to setup permissions
      importCsv("energy/energy_consumers.csv", 1000000, true,
            "org_1", datacoreApi, line -> {
         String countryId = line[12]; // geocofr:idIso
         String cityId = countryId + '/' + line[13] + '/' + line[14]; // geon3fr:idIso  geocifr:name
         String consumerId = hashCodeId(line[4]);
         DCResource r = DCResource.create(null, "persid:Identity_0")
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
               .set("adrpost:postName", UriService.buildUri("geocifr:Commune_0", cityId))
               .set("adrpost:country", UriService.buildUri("geocofr:Pays_0", countryId));
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
      importCsv("energy/energy_contracts.csv", 1000000, true,
            DCProject.OASIS_SANBOX, datacoreApi, line -> {
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
         DCResource cr = DCResource.create(null, "enercontr:EnergyConsumptionContract_0")
               .set("enercontr:provider", UriService.buildUri("orgfr:Organisation_0", providerId))
               .set("enercontr:consumer", UriService.buildUri("persid:Identity_0", consumerId))
               .set("odisp:displayName", line[3])
               .set("enercontr:customerKey", customerKey);
         cr.setUriFromId(containerUrl, contractId);
               
         return new DCResource[] { cr };
      });

      //String csvResourcePath = "energy/energy_contracts.csv";
      
      //String consumptionsCsvPath = "/home/mdutoo/dev/oasis/workspace/ozwillo-ozenergy/energy-ok-import-datacore.csv";
      String consumptionsCsvPath = "energy/energy-ok-import-datacore.csv";
      final List<String> customerKeysWithoutContract = new ArrayList<String>();
      importCsv(consumptionsCsvPath, 1000000, false,
            DCProject.OASIS_SANBOX, datacoreApi, line -> {
         String customerKey = line[0];
         String contractId = customerKeyToContractId.get(customerKey); // "FR/49015839100014/964549036";
         if (contractId == null) {
            customerKeysWithoutContract.add(customerKey);
            return null;
         }
         DCResource r = DCResource.create(null, "enercons:EnergyConsumption_0")
               .set("enercons:contract", UriService.buildUri("enercontr:EnergyConsumptionContract_0", contractId))
               .set("enercons:date", DateTime.parse(line[1])) // joda rather than java 8 date (not supported locally)
               .set("enercons:globalKWH", parseNumber(line[7]));
         
         r.setUriFromId(containerUrl, contractId + '/' + r.get("enercons:date"));
         return new DCResource[] { r };
      });
      
      System.out.println("Not imported " + customerKeysWithoutContract.size()
         + " consumptions having no contract with the following customerKeys: "
         + new HashSet<String>(customerKeysWithoutContract));
      System.out.println("serverRatherThanClient=" + serverRatherThanClient);
   }
   

   private Number parseNumber(String numberString) {
      try {
         return Double.parseDouble(numberString);
      } catch (NumberFormatException e) {
         // ex. on "0"
         return Long.parseLong(numberString);
      }
   }


   public String hashCodeId(String s) {
      return hashCode(s) + "";
   }
   public int hashCode(String s) {
      int hash = 0;
      int len = s.length();
      if (len == 0) return hash;
      for (int i = 0; i < len; i++) {
         char chr   = s.charAt(i);
         hash  = ((hash << 5) - hash) + chr;
         hash |= 0; // Convert to 32bit integer
      }
      return hash;
   }

   public void importCsv(String csvFileOrClassPath, int maxLineNb, boolean checkIfExists,
         String project, DatacoreApi datacoreApi,
         Function<String[], ? extends DCResource[]> indexedFieldSetResourceMapper) throws Exception {
      if (project == null) {
         project = DCProject.OASIS_SANBOX;
      }
      SimpleRequestContextProvider.setSimpleRequestContext(new ImmutableMap.Builder<String, Object>()
            .put(DatacoreApi.PROJECT_HEADER, project).build());
      
      InputStream csvIn = getClass().getClassLoader().getResourceAsStream(csvFileOrClassPath);
      if (csvIn == null) { // in case of classpath resource only
         //throw new RuntimeException("Unable to find in classpath CSV resource");
         File csvFile = new File(csvFileOrClassPath);
         if (!csvFile.canRead()) {
            throw new RuntimeException("Unable to find/read in classpath or file CSV resource " + csvFileOrClassPath);
         }
         csvIn = new FileInputStream(csvFile);
      }
      
      long startTime = System.currentTimeMillis();
      int resourceBatchSize = 1000;
      
      CSVReader csvReader = null;
      try  {
         csvReader = new CSVReader(new InputStreamReader(csvIn), ',');
         String [] line;
         csvReader.readNext(); // skip header TODO parse & map it
         int resourceNb = 0;
         int ln;
         List<DCResource> resourcesToPost = new ArrayList<DCResource>(150);
         for (ln = 0; (line = csvReader.readNext()) != null && ln < maxLineNb; ln++) {
            // schedule post :
            DCResource[] mappedResources = indexedFieldSetResourceMapper.apply(line);
            if (mappedResources == null || mappedResources.length == 0) {
               continue;
            }
            resourcesToPost.addAll(Arrays.asList(mappedResources));
            
            //if ((ln + 1) % lineBatchSize == 0) {
            if (checkIfExists) { // prevents batch
               for (DCResource r : mappedResources) {
                  try {
                     datacoreApi.getData(r.getModelType(), r.getId(), null);
                  } catch (NotFoundException rnfex) {
                     //datacoreApiClient.postDataInType(r);
                     try {
                        datacoreApi.postAllDataInType(Arrays.asList(new DCResource[]{ r }), r.getModelType());
                     } catch (WebApplicationException e) {
                        if (e.getResponse().getStatus() / 100 == 2) {} else throw e;
                     }
                     resourceNb++;
                  } catch (WebApplicationException e) {
                     if (e.getResponse().getStatus() / 100 == 2) {} else throw e;
                  } // else don't repost it if already there (empty db for that)
               }
               
            } else if (resourcesToPost.size() >= resourceBatchSize) {
               // ASSUMING THERE ARE NONE YET (else requires getData which forbids batch) TODO test it
               try {
                  datacoreApi.postAllData(resourcesToPost);
               } catch (WebApplicationException e) {
                  if (e.getResponse().getStatus() / 100 == 2) {} else throw e;
               }
               resourceNb += resourcesToPost.size();
               resourcesToPost.clear();
            }
            
            // TODO LATER could catch around each line to allow importing other ones, and return all errors
         }
         
         // post batch remainder :
         if (!checkIfExists && !resourcesToPost.isEmpty()) {
            try {
               // ASSUMING THERE ARE NONE YET (else requires getData which forbids batch) TODO test it
               datacoreApi.postAllData(resourcesToPost);
            } catch (WebApplicationException e) {
               if (e.getResponse().getStatus() / 100 == 2) {} else throw e;
            }
            resourceNb += resourcesToPost.size();
         }

         long endTime = System.currentTimeMillis();
         System.out.println("Took " + (endTime - startTime) + " ms to upload "
               + resourceNb + " resources out of " + ln + " lines from CSV file " + csvFileOrClassPath);

      } catch (WebApplicationException waex) {
         Response r = waex.getResponse();
         String errMsg = UnitTestHelper.readBodyAsString(waex);
         throw new RuntimeException("HTTP " + r.getStatus()
               + " web app error reading classpath CSV resource " + csvFileOrClassPath
               + " :\n" + r.getStringHeaders() + "\n" + errMsg + "\n\n", waex);
         
      /*} catch (Exception ex) {
         throw new RuntimeException("Error reading classpath CSV resource " + csvResourcePath, ex);*/
       
      } finally {
         try {
            csvReader.close();
         } catch (IOException e) {
           // TODO log
         }
      }

   }

   
   ///@Test
   public void testDraft() throws Exception {
      // set sample project :
      SimpleRequestContextProvider.setSimpleRequestContext(new ImmutableMap.Builder<String, Object>()
            .put(DatacoreApi.PROJECT_HEADER, DCProject.OASIS_SANBOX).build());
      
      authenticationService.loginAs("guest");
      int maxLineNb = 1000000; // 5

      String csvResourcePath = "/home/mdutoo/dev/oasis/workspace/ozwillo-ozenergy/energy-ok-import-datacore.csv";
      //InputStream csvIn = getClass().getClassLoader().getResourceAsStream(csvResourcePath );
      InputStream csvIn = new FileInputStream(csvResourcePath);
      /*if (csvIn == null) {
         throw new RuntimeException("Unable to find in classpath CSV resource " + csvResourcePath);
      }*/
      CSVReader csvReader = null;
      try  {
         csvReader = new CSVReader(new InputStreamReader(csvIn), ',');
         String [] line;
         csvReader.readNext(); // skip header
         for (int ln = 0; (line = csvReader.readNext()) != null && ln < maxLineNb; ln++) {
            try {
            // filling company's provided props :
            DCResource r = DCResource.create(null, "mod:Energy_0")
                  .set("mod_Energy:CUSTOMER_KEY", Integer.parseInt(line[0], 10))
                  .set("mod_Energy:date_mesure", line[1])
                  .set("mod_Energy:General_Supply_KWH", Double.parseDouble(line[7]));
            
            // once props are complete, build URI out of them and schedule post :
            r.setUriFromId(containerUrl, r.get("mod_Energy:CUSTOMER_KEY").toString()
                  + '/' + r.get("mod_Energy:date_mesure"));
            try {
               datacoreApiImpl.getData(r.getModelType(), r.getId(), null);
            } catch (NotFoundException rnfex) {
               //datacoreApiClient.postDataInType(r);
               datacoreApiImpl.postDataInType(r, r.getModelType());
            } // else don't repost it if already there (empty db for that)
            } catch (WebApplicationException nfex) {
               if (nfex.getResponse().getStatus() == 201) {
                  // normal
               } else {
                  throw nfex;
               }
            } catch (NumberFormatException nfex) {
               throw new RuntimeException("NumberFormatException " + line[7]);
            }
         }

      } catch (WebApplicationException waex) {
         throw new RuntimeException("HTTP " + waex.getResponse().getStatus()
               + " web app error reading classpath CSV resource " + csvResourcePath
               + " :\n" + waex.getResponse().getStringHeaders() + "\n"
               + ((waex.getResponse().getEntity() != null) ? waex.getResponse().getEntity() + "\n\n" : ""), waex);
         
      } catch (Exception ex) {
         throw new RuntimeException("Error reading classpath CSV resource " + csvResourcePath, ex);
         
      } finally {
         try {
            csvReader.close();
         } catch (IOException e) {
           // TODO log
         }
      }

   }
   

}
