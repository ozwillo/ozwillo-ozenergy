package org.ozwillo.energy.service;

import java.util.List;

import org.oasis_eu.spring.datacore.DatacoreClient;
import org.oasis_eu.spring.datacore.model.DCOperator;
import org.oasis_eu.spring.datacore.model.DCOrdering;
import org.oasis_eu.spring.datacore.model.DCQueryParameters;
import org.oasis_eu.spring.datacore.model.DCResource;
import org.oasis_eu.spring.kernel.service.UserInfoService;
import org.ozwillo.energy.AjaxServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class ContractService {

   private static final Logger logger = LoggerFactory.getLogger(AjaxServices.class);

   /** to get contract of current user */
   @Autowired
   private UserInfoService userInfoService;
   @Autowired
   private DatacoreClient datacore;
   @Value("${application.devmode:false}")
   private boolean devmode;
   @Value("${application.security.noauthdevmode:false}")
   private boolean noauthdevmode;

   @Value("${datacore.data.mongodb.energyProject}")
   private String energyProject;
   @Value("${datacore.data.mongodb.energyContractCollection}")
   private String energyContractCollection;
   @Value("${datacore.data.mongodb.energyConsumptionCollection}")
   private String energyConsumptionCollection;

   public String getCurrentUserContract() {
	  String kernelUserEmail = null;
	  try {
		  kernelUserEmail = userInfoService.currentUser().getEmail();
	  } catch (Exception e) {
		  if (devmode && noauthdevmode) {
			  logger.info("devmode && noauthdevmode activated - No user found : switching to default user jacques.colard@gmail.com");
		  }
		  else {
			  logger.info("devmode or noauthdevmode not activated - No user found : returning no contract");
			  return null;
		  }
	  }
      return getUserContractFromEmail(kernelUserEmail);
   }

   public String getUserContractFromEmail(String email) {
      if (email == null) {
         // noauthdevmode, return default
         // (otherwise this page can't be reached unless logged in)
    	 // This contract is associated with "jacques.colard@gmail.com"
         return "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/55208131766522/39080212";
      }

      // finding users's persid:Person in the Datacore (or could do it using aggregation) :
      // (requires to put the current user's email in the demo energy_consumers.csv and reimport it)
      try {
         List<DCResource> personFound = datacore.findResources("org_1", "persid:Person_0",
               new DCQueryParameters("persid:email", DCOrdering.DESCENDING, DCOperator.EQ, "\"" + email + "\""), 0, 2);
         if (personFound.size() == 1) {
            DCResource user = personFound.get(0);
            List<DCResource> contractFound = datacore.findResources(energyProject, energyContractCollection,
                  new DCQueryParameters("enercontr:consumer", DCOperator.EQ, "\"" + user.getUri() + "\""), 0, 2);
            if (contractFound.size() == 1) {
               return contractFound.get(0).getUri();
            }
         }
      } catch (HttpClientErrorException httpcex) {
         logger.warn("HTTP client error finding contract in Datacore, "
               + "probably not found custom energy models", httpcex.getMessage());
      }

      if (devmode) {
         // else using hardcoded mapping for demo :
         switch (email) {
         case "marc.dutoo@openwide.fr":
            return "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/964549036";
         case "marc.dutoo@laposte.net":
            return "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/421132066";
         }
         // else default :
         return "http://data.ozwillo.com/dc/type/enercontr:EnergyConsumptionContract_0/FR/49015839100014/39080212";
      }

      // TODO LATER offer to create a contract
      throw new RuntimeException("No contract for current user " + email);
   }

}
