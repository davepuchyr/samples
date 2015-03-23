// $Id: UpdateVO.java 34 2009-12-19 18:42:32Z dave $
package com.netmorpher.client.model.vo;

import java.util.ArrayList;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;


/**
 * @author dave
 *
 */
public class UpdateVO extends InsertVO {
   private String _clause = null;

   
   public UpdateVO( String notice, String table, ArrayList<String> columns, String clause ) {
      super( notice, table, columns );
      
      _clause = clause;
   }
   
   
   public UpdateVO( String notice, String table, ArrayList<String> columns ) {
      super( notice, table, columns );
   }
   
   
   public void setClause( String clause ) {
      _clause = clause;
   }
   
   
   @Override
   protected void accept( JSONObject jsono ) {
      super.accept( jsono );
      
      jsono.put( "action", new JSONString( "update" ) );
      
      if ( _clause != null ) jsono.put( "clause", new JSONString( _clause ) );
   }
}
