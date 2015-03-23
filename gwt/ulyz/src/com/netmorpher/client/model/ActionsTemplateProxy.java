// $Id: ActionsTemplateProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.model.vo.TemplateVO;

/**
 * @author dave
 *
 */
public class ActionsTemplateProxy extends DatabaseProxy<TemplateVO> {
   public static final String NAME = "ActionsTemplateProxy";

   private ArrayList<String> _keys = new ArrayList<String>();
   private ArrayList<String> _keywords = null;
   private HashMap<String, String> _map = new HashMap<String, String>();


   public ActionsTemplateProxy() {
      super( NAME );
   }


   public ActionsTemplateProxy( String name ) {
      super( name );
   }


   public void map( JsArray<TemplateVO> rows ) {
      _keys.clear();
      _map.clear();

      HashSet<String> keywords = new HashSet<String>();

      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         TemplateVO vo = (TemplateVO) rows.get( i );
         String key = vo.getField(), value = vo.getValue();
         
         _keys.add( key );
         _map.put( key, value );
         keywords.addAll( parseKeywords( value ) );
      }
      
      HashSet<String> filtered = new HashSet<String>();
      
      for ( String key : keywords ) if ( !_keys.contains( key ) ) filtered.add( key ); // filter out template keys
      
      _keywords = new ArrayList<String>( filtered.size() );
      _keywords.addAll( filtered );

      Collections.sort( _keywords );
      Collections.sort( _keys );
   }


   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final String getValue( String key ) {
      return _map.get( key );
   }
   
   
   public final ArrayList<String> getKeywords() {
      return _keywords;
   }
   
   
   public final ArrayList<String> parseKeywords( String value ) {
      return new ArrayList<String>( Arrays.asList( match( value ).split( "\\|" ) ) ); // HARD-CODED delimiter
   }
      
   
   // JSNI methods
   private final native String match( String s ) /*-{
      var keywords = s.match( /\{\{\S+\}\}/g ); // HARD-CODED regex
      return keywords ? keywords.join( "|" ) : ""; // HARD-CODED delimiter; hack to bridge js to java
   }-*/;
}

