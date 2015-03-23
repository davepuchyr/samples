// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class AuthenticatedPlace extends PlaceAdapter {
   static final public AuthenticatedPlace INSTANCE = new AuthenticatedPlace();


   @Prefix( PlaceHistoryMapper.PREFIX_AUTHENTICATED )
   static public class Tokenizer implements PlaceTokenizer<AuthenticatedPlace> {
      @Override public AuthenticatedPlace getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( AuthenticatedPlace place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
