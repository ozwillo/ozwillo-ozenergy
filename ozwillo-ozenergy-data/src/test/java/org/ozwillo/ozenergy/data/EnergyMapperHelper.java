package org.ozwillo.ozenergy.data;

public class EnergyMapperHelper {

   public static Number parseNumber(String numberString) {
      try {
         return Double.parseDouble(numberString);
      } catch (NumberFormatException e) {
         // ex. on "0"
         return Long.parseLong(numberString);
      }
   }

   public static String hashCodeId(String s) {
      return hashCode(s) + "";
   }
   public static int hashCode(String s) {
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
   
}
