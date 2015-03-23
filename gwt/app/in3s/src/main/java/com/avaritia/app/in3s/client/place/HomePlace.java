// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class HomePlace extends PlaceAdapter {
   static final public HomePlace INSTANCE = new HomePlace();


   @Prefix( PlaceHistoryMapper.PREFIX_HOME )
   static public class Tokenizer implements PlaceTokenizer<HomePlace> {
      @Override public HomePlace getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( HomePlace place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
