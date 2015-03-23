// $Id: InsertVO.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.model.vo;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

/**
 * @author dave
 *
 */
public class InsertVO extends DatabaseWriteVO {
   private HashMap<String, String> _columns2values = new HashMap<String, String>();

   
   public InsertVO( String notice, String table, ArrayList<String> columns ) {
      super( notice, table );
      
      for ( String column : columns ) _columns2values.put( column, null );
   }

   
   public void put( String column, String value ) {
      if ( !_columns2values.containsKey( column ) ) {
         Window.alert( "column '" + column + "' does not exist!" );
         return;
      }
      
      _columns2values.put( column, value );
   }
   
   
   @Override
   protected void accept( JSONObject jsono ) {
      JSONArray columns = new JSONArray();
      JSONArray values = new JSONArray();
      int i = 0;
      
      for ( String column : _columns2values.keySet() ) {
         columns.set( i, new JSONString( column ) );
         String value = _columns2values.get( column );
         values.set( i++, value == null ? null : new JSONString( value ) ); // note increment
      }

      jsono.put( "action", new JSONString( "insert" ) );
      jsono.put( "columns", columns );
      jsono.put( "values", values );
   }
}

