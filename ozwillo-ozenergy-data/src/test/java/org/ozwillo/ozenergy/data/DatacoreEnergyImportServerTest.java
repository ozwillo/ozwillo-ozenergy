package org.ozwillo.ozenergy.data;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.oasis.datacore.core.security.mock.LocalAuthenticationService;
import org.oasis.datacore.rest.server.DatacoreApiImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
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
@ContextConfiguration(locations = { "classpath:oasis-datacore-ozenergy-data-context.xml" }) // NOT classpath:oasis-datacore-rest-server-test-context.xml
//@FixMethodOrder(MethodSorters.NAME_ASCENDING) // else random since java 7 NOT REQUIRED ANYMORE
public class DatacoreEnergyImportServerTest extends DatacoreEnergyImportTestBase {

   /** for testing purpose */
   @Autowired
   @Qualifier("datacoreApiImpl")
   private DatacoreApiImpl datacoreApiImpl;
   @Autowired
   private LocalAuthenticationService authenticationService;
//   /** for testing purpose */
//   @Autowired
//   private LdpEntityQueryServiceImpl ldpEntityQueryServiceImpl;

   @Before
   public void setup() throws Exception {
      authenticationService.loginAs("admin"); // works only on server-side (on client side, also requires Bearer/Basic credentials)
      datacoreApi = datacoreApiImpl;
   }

   ///@Test
//   public void testDraft() throws Exception {
//      // set sample project :
//      SimpleRequestContextProvider.setSimpleRequestContext(new ImmutableMap.Builder<String, Object>()
//            .put(DatacoreApi.PROJECT_HEADER, DCProject.OASIS_SANBOX).build());
//
//      authenticationService.loginAs("guest");
//      int maxLineNb = 1000000; // 5
//
//      String csvResourcePath = "/home/mdutoo/dev/oasis/workspace/ozwillo-ozenergy/energy-ok-import-datacore.csv";
//      //InputStream csvIn = getClass().getClassLoader().getResourceAsStream(csvResourcePath );
//      InputStream csvIn = new FileInputStream(csvResourcePath);
//      /*if (csvIn == null) {
//         throw new RuntimeException("Unable to find in classpath CSV resource " + csvResourcePath);
//      }*/
//      CSVReader csvReader = null;
//      try  {
//         csvReader = new CSVReader(new InputStreamReader(csvIn), ',');
//         String [] line;
//         csvReader.readNext(); // skip header
//         for (int ln = 0; (line = csvReader.readNext()) != null && ln < maxLineNb; ln++) {
//            try {
//            // filling company's provided props :
//            DCResource r = DCResource.create(null, "mod:Energy_0")
//                  .set("mod_Energy:CUSTOMER_KEY", Integer.parseInt(line[0], 10))
//                  .set("mod_Energy:date_mesure", line[1])
//                  .set("mod_Energy:General_Supply_KWH", Double.parseDouble(line[7]));
//
//            // once props are complete, build URI out of them and schedule post :
//            r.setUriFromId(containerUrl, r.get("mod_Energy:CUSTOMER_KEY").toString()
//                  + '/' + r.get("mod_Energy:date_mesure"));
//            try {
//               datacoreApiImpl.getData(r.getModelType(), r.getId(), null);
//            } catch (NotFoundException rnfex) {
//               //datacoreApiClient.postDataInType(r);
//               datacoreApiImpl.postDataInType(r, r.getModelType());
//            } // else don't repost it if already there (empty db for that)
//            } catch (WebApplicationException nfex) {
//               if (nfex.getResponse().getStatus() == 201) {
//                  // normal
//               } else {
//                  throw nfex;
//               }
//            } catch (NumberFormatException nfex) {
//               throw new RuntimeException("NumberFormatException " + line[7]);
//            }
//         }
//
//      } catch (WebApplicationException waex) {
//         throw new RuntimeException("HTTP " + waex.getResponse().getStatus()
//               + " web app error reading classpath CSV resource " + csvResourcePath
//               + " :\n" + waex.getResponse().getStringHeaders() + "\n"
//               + ((waex.getResponse().getEntity() != null) ? waex.getResponse().getEntity() + "\n\n" : ""), waex);
//
//      } catch (Exception ex) {
//         throw new RuntimeException("Error reading classpath CSV resource " + csvResourcePath, ex);
//
//      } finally {
//         try {
//            csvReader.close();
//         } catch (IOException e) {
//           // TODO log
//         }
//      }
//
//   }

}
