// $Id: DeleteVO.java 34 2009-12-19 18:42:32Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;


/**
 * @author dave
 *
 */
public class DeleteVO extends DatabaseWriteVO {
   private String _clause = null;

   
   public DeleteVO( String table, String notice, String clause ) {
      super( table, notice );
      
      _clause = clause;
   }
   
   
   public DeleteVO( String table, String notice ) {
      super( table, notice );
   }
   
   
   public void setClause( String clause ) {
      _clause = clause;
   }
   
   
   @Override
   protected void accept( JSONObject jsono ) {
      jsono.put( "action", new JSONString( "delete" ) );
      
      if ( _clause != null ) jsono.put( "clause", new JSONString( _clause ) );
   }
}
