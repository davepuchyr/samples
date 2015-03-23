// $Id$
package com.avaritia.app.in3s.client.place;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;


public class SettingsPlace extends PlaceAdapter {
   static final public SettingsPlace INSTANCE = new SettingsPlace();


   @Prefix( PlaceHistoryMapper.PREFIX_SETTINGS )
   static public class Tokenizer implements PlaceTokenizer<SettingsPlace> {
      @Override public SettingsPlace getPlace( String token ) {
         return INSTANCE;
      }


      @Override public String getToken( SettingsPlace place ) {
         return "";
      }
   }


   @Override public Activity accept( PlaceAdapterVisitor visitor ) {
      return visitor.visit( this );
   }
}
