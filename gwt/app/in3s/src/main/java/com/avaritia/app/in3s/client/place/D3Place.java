// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class D3Place extends PlaceAdapter {
   static final public D3Place INSTANCE = new D3Place();


   @Prefix( PlaceHistoryMapper.PREFIX_D3 )
   static public class Tokenizer implements PlaceTokenizer<D3Place> {
      @Override public D3Place getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( D3Place place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
