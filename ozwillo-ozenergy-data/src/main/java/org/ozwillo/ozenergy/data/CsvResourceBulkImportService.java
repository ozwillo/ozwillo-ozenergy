package org.ozwillo.ozenergy.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.oasis.datacore.common.context.SimpleRequestContextProvider;
import org.oasis.datacore.core.entity.query.ldp.LdpEntityQueryServiceImpl;
import org.oasis.datacore.rest.api.DCResource;
import org.oasis.datacore.rest.api.DatacoreApi;
import org.oasis.datacore.rest.api.util.UnitTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import au.com.bytecode.opencsv.CSVReader;


/**
 * Imports resources from CSV.
 * Requires model to have been already imported (ex. using the Playground Import tool).
 *
 * - spring JdbcTemplate-like architecture, TODO LATER even integrate with Spring Batch ?
 * - can use either REST client or server DatacoreApi
 * - either batch (REQUIRES TO DELETE EXISTING RESOURCES) or checkExists mode
 * - supports file or classpath resource
 * - auth : login with the right user must be done explicitly using helpers
 *
 * @author mdutoo
 *
 */
//@Component // instanciated in XML for both client & server datacoreApi
public class CsvResourceBulkImportService {

   private static final Logger logger = LoggerFactory.getLogger(CsvResourceBulkImportService.class);

   private DatacoreApi datacoreApi;


   // inspired by Spring (JdbcTemplate, Spring Batch for flat file)
   public interface IndexedFieldSetMapper<T> {
      public List<T> map(String[] indexedFieldSet);
   }


   /**
    * Imports from given CSV.
    * Auth login must be done explicitly outside, or in checkIfExists mode for each resource
    * @param csvFileOrClassPath
    * @param maxLineNb
    * @param checkIfExists
    * @param project (in checkIfExists mode, can also be set explicitly on each resource)
    * @param indexedFieldSetResourceMapper
    * @return success message with info about what was imported, TODO rather object
    * @throws Exception
    */
   public String importCsv(String csvFileOrClassPath, int maxLineNb, boolean checkIfExists,
         String project,
         Function<String[], ? extends DCResource[]> indexedFieldSetResourceMapper) throws Exception {
      if (project == null) {
         project = "energy_0";
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
         String successMsg = "Took " + (endTime - startTime) + " ms to upload "
               + resourceNb + " resources out of " + ln + " lines from CSV file " + csvFileOrClassPath;
         logger.error(successMsg);
         return successMsg;

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


   public DatacoreApi getDatacoreApi() {
      return datacoreApi;
   }
   public void setDatacoreApi(DatacoreApi datacoreApi) {
      this.datacoreApi = datacoreApi;
   }

}
