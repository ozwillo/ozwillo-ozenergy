package org.ozwillo.ozenergy.data;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.oasis.datacore.rest.client.cxf.mock.AuthenticationHelper;
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
@ContextConfiguration(locations = { "classpath:oasis-datacore-ozenergy-data-client-context.xml" }) // NOT classpath:oasis-datacore-rest-server-test-context.xml
//@FixMethodOrder(MethodSorters.NAME_ASCENDING) // else random since java 7 NOT REQUIRED ANYMORE
public class DatacoreEnergyImportClientTest extends DatacoreEnergyImportTestBase {


   @Before
   public void setup() throws Exception {
       AuthenticationHelper.loginBasicAsAdmin(); // works only on client side in devmode
       datacoreApi = datacoreApiClient;
   }
  
}
