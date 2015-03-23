// $Id: DatabaseWriteVO.java 34 2009-12-19 18:42:32Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * @author dave
 *
 */
public abstract class DatabaseWriteVO {
   abstract protected void accept( JSONObject jsono );
   
   
   private String _notice = null;
   private String _table = null;

   
   public DatabaseWriteVO( String notice, String table ) {
      _notice = notice;
      _table = table;
   }
   
   
   public String getNotice() {
      return _notice;
   }
   
   
   public String getJSON() {
      JSONObject jsono = new JSONObject();

      jsono.put( "table", new JSONString( _table ) );

      accept( jsono );

      return jsono.toString();
   }
}
