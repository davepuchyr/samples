// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.lib.java.fsm.client.HasToken;


abstract public class DelimitedPlace extends PlaceAdapter implements HasToken {
   static public String DELIMITER = ",";


   private String token;
   private String[] values;


   public DelimitedPlace( String token ) {
      setToken( token );
   }


   @Override public String getToken() {
      return token;
   }


   @Override public void setToken( String token ) {
      this.token = token;
      this.values = token.split( DELIMITER );
   }


   public String[] getValues() {
      return values;
   }
}
