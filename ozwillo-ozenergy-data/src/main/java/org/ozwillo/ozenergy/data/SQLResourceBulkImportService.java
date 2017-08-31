package org.ozwillo.ozenergy.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.oasis.datacore.common.context.SimpleRequestContextProvider;
import org.oasis.datacore.rest.api.DCResource;
import org.oasis.datacore.rest.api.DatacoreApi;
import org.oasis.datacore.rest.api.util.UnitTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

/**
 * Imports resources from PostgreSQL DB.
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
public class SQLResourceBulkImportService {

   private static final Logger logger = LoggerFactory.getLogger(SQLResourceBulkImportService.class);

   private DatacoreApi datacoreApi;


   // inspired by Spring (JdbcTemplate, Spring Batch for flat file)
   public interface IndexedFieldSetMapper<T> {
      public List<T> map(String[] indexedFieldSet);
   }


   /**
    * Imports from given PostgreSQL DB.
    * Auth login must be done explicitly outside, or in checkIfExists mode for each resource
    * @param dbURL
    * @param maxLineNb
    * @param checkIfExists
    * @param project (in checkIfExists mode, can also be set explicitly on each resource)
    * @param indexedFieldSetResourceMapper
    * @return success message with info about what was imported, TODO rather object
    * @throws Exception
    */
   public String bridgeDB(String dbURL, String dbUsername, String dbPassword, int maxLineNb, boolean checkIfExists,
         String project,
         Function<String[], ? extends DCResource[]> indexedFieldSetResourceMapper) throws Exception {
      if (project == null) {
         project = "energy_0";
      }
      SimpleRequestContextProvider.setSimpleRequestContext(new ImmutableMap.Builder<String, Object>()
            .put(DatacoreApi.PROJECT_HEADER, project).build());

      long startTime = System.currentTimeMillis();
      int resourceBatchSize = 1000;
      
      ResultSet rs = requestDB(dbURL, dbUsername, dbPassword);

      try  {         
         String[] line;

         int resourceNb = 0;
         int ln;
         List<DCResource> resourcesToPost = new ArrayList<DCResource>(150);
         for (ln = 0; rs.next() && ln < maxLineNb; ln++) {
            
        	line = new String[8];
	        line[0] = rs.getString("email");
	        line[1] = rs.getString("project_id");
	        line[2] = rs.getString("device_id");
	        line[3] = rs.getString("pin");
	        line[4] = rs.getString("pintype");
	        line[5] = rs.getString("ts");
	        line[6] = rs.getString("stringvalue");
	        line[7] = rs.getString("doublevalue");
        	 
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
               + resourceNb + " resources out of " + ln + " lines from DB table " + dbURL;
         logger.error(successMsg);
         return successMsg;

      } catch (WebApplicationException waex) {
         Response r = waex.getResponse();
         String errMsg = UnitTestHelper.readBodyAsString(waex);
         throw new RuntimeException("HTTP " + r.getStatus()
               + " web app error reading DB table" + dbURL
               + " :\n" + r.getStringHeaders() + "\n" + errMsg + "\n\n", waex);

      } finally {
         try {
        	 rs.close();
         } catch (SQLException e) {
           logger.error(e.getClass().getName()+": "+ e.getMessage());
         }
      }

   }

   public DatacoreApi getDatacoreApi() {
      return datacoreApi;
   }
   public void setDatacoreApi(DatacoreApi datacoreApi) {
      this.datacoreApi = datacoreApi;
   }
   
   private ResultSet requestDB(String dbURL, String dbUsername, String dbPassword) {
      Connection c = null;
      Statement stmt = null;
      ResultSet rs = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
    		 .getConnection(dbURL, dbUsername, dbPassword);
         c.setAutoCommit(false);
         System.out.println("Opened database successfully");
         
         stmt = c.createStatement();
         rs = stmt.executeQuery( "SELECT * FROM reporting_raw_data;" );
         
         stmt.close();
         c.close();
      } catch ( Exception e ) {
    	  logger.error( e.getClass().getName()+": "+ e.getMessage() );
         System.exit(0);
      }
      logger.info("Operation done successfully");
      
      return rs;
   }

}
