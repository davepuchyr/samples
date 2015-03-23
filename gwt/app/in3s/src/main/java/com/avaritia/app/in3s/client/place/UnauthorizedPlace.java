// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class UnauthorizedPlace extends PlaceAdapter {
   static final public UnauthorizedPlace INSTANCE = new UnauthorizedPlace();


   @Prefix( PlaceHistoryMapper.PREFIX_UNAUTHORIZED )
   static public class Tokenizer implements PlaceTokenizer<UnauthorizedPlace> {
      @Override public UnauthorizedPlace getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( UnauthorizedPlace place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
