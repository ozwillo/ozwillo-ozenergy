package org.ozwillo.ozenergy.data;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.oasis.datacore.rest.client.cxf.mock.AuthenticationHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Injects energy data from Blynk server PostgreSQL DB.
 *
 * Requires model to have been already imported (ex. using the Playground Import tool).
 * Simply extends the DatacoreEnergyBridgeTestBase to add auth appropriate for client-sided
 * importation.
 *
 * @author brenault
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:oasis-datacore-ozenergy-data-client-context.xml" }) // NOT classpath:oasis-datacore-rest-server-test-context.xml
public class DatacoreEnergyBridgeClientTest extends DatacoreEnergyBridgeTestBase {


   @Before
   public void setup() throws Exception {
       AuthenticationHelper.loginBasicAsAdmin(); // works only on client side in devmode
       datacoreApi = datacoreApiClient;
   }
  
}
