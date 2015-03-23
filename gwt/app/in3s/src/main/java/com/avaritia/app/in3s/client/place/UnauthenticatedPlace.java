// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class UnauthenticatedPlace extends PlaceAdapter {
   static final public UnauthenticatedPlace INSTANCE = new UnauthenticatedPlace();


   @Prefix( PlaceHistoryMapper.PREFIX_UNAUTHENTICATED )
   static public class Tokenizer implements PlaceTokenizer<UnauthenticatedPlace> {
      @Override public UnauthenticatedPlace getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( UnauthenticatedPlace place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
