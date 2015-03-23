// $Id: AccountsProxy.java 31 2009-12-18 17:09:45Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.model.vo.MappingVO;

/**
 * @author dave
 *
 */
public class MappingsProxy extends DatabaseProxy<MappingVO> {
   public static final String NAME = "MappingsProxy";

   private ArrayList<String> _keys = null;
   private HashMap<String, String> _map = new HashMap<String, String>();


   public MappingsProxy() {
      super( NAME );
   }


   public void map( JsArray<MappingVO> rows, ArrayList<String> fields ) {
      _keys = fields;
      _map.clear();

      HashSet<String> blanks = new HashSet<String>();
      
      blanks.addAll( _keys );
      
      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         MappingVO vo = rows.get( i );
         String key = vo.getField();
         
         blanks.remove( key );
         _map.put( key, vo.getValue() );
      }
      
      for ( String key : blanks ) _map.put( key, "" ); // append blanks
   }


   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final String getValue( String key ) {
      return _map.get( key );
   }
}

