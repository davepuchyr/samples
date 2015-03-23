// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class AuthorizedPlace extends PlaceAdapter {
   static final public AuthorizedPlace INSTANCE = new AuthorizedPlace();


   @Prefix( PlaceHistoryMapper.PREFIX_AUTHORIZED )
   static public class Tokenizer implements PlaceTokenizer<AuthorizedPlace> {
      @Override public AuthorizedPlace getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( AuthorizedPlace place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
