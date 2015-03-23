// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class HelpPlace extends DelimitedPlace {
   @Prefix( PlaceHistoryMapper.PREFIX_HELP )
   static public class Tokenizer implements PlaceTokenizer<HelpPlace> {
      @Override public HelpPlace getPlace( String token ) {
         return new HelpPlace( token );
      }


      @Override public String getToken( HelpPlace place ) {
         return place.getToken();
      }
   }


   public HelpPlace( String token ) {
      super( token );
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
