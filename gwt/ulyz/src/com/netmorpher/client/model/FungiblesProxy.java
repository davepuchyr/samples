// $Id: AccountsProxy.java 31 2009-12-18 17:09:45Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.puremvc.java.multicore.interfaces.IProxy;
import org.puremvc.java.multicore.patterns.proxy.Proxy;

/**
 * @author dave
 *
 */
public class FungiblesProxy extends Proxy implements IProxy {
   public static final String NAME = "FungiblesProxy";

   private ArrayList<String> _keys = new ArrayList<String>();
   private HashMap<String, ArrayList<String>> _map = new HashMap<String, ArrayList<String>>();


   public FungiblesProxy() {
      super( NAME );
   }


   public void update( HashMap<String, ArrayList<String>> oneToMany, SynonymsProxy proxySynonyms ) {
      _keys.clear();
      _map.clear();
      
      map( oneToMany, proxySynonyms );

      Collections.sort( _keys );
   }
   
   
   private void map( HashMap<String, ArrayList<String>> oneToMany, SynonymsProxy proxySynonyms ) {
      HashMap<String, ArrayList<String>> need = new HashMap<String, ArrayList<String>>();
      
      for ( String key : oneToMany.keySet() ) {
         if ( _keys.contains( key ) ) continue;
         
         ArrayList<String> values = oneToMany.get( key );
         
         for ( String value : values ) {
            for ( String newkey : parseValue( value ) ) {
               if ( _keys.contains( newkey ) || need.containsKey( newkey ) ) continue;
               
               ArrayList<String> synonyms = proxySynonyms.getSynonyms( newkey );

               need.put( newkey, synonyms != null ? synonyms : new ArrayList<String>() );
            }
         }
         
         _keys.add( key );
         _map.put( key, values );
      }
      
      if ( !need.isEmpty() ) map( need, proxySynonyms );
   }


   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final ArrayList<String> getValue( String key ) {
      return _map.get( key );
   }
   
   
   public final String[] parseValue( String value ) {
      return match( value ).split( "\\|" ); // HARD-CODED delimiter
   }
   
   
   // JSNI methods
   private final native String match( String s ) /*-{
      var keywords = s.match( /\{\{\S+\}\}/g ); // HARD-CODED regex
      return keywords ? keywords.join( "|" ) : ""; // HARD-CODED delimiter; hack to bridge js to java
   }-*/;
}

