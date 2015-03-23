// $Id: SitesProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.DatabaseWriteVO;
import com.netmorpher.client.model.vo.DeleteVO;
import com.netmorpher.client.model.vo.InsertVO;
import com.netmorpher.client.model.vo.SiteVO;
import com.netmorpher.client.model.vo.UpdateVO;

/**
 * @author dave
 *
 */
public class SitesProxy extends DatabaseProxy<SiteVO> {
   public static final String NAME = "SitesProxy";
   
   private ArrayList<String> _keys = new ArrayList<String>();
   private HashMap<String, ArrayList<String>> _map = new HashMap<String, ArrayList<String>>();


   public SitesProxy() {
      super( NAME );
   }
   
   
   public void map( JsArray<SiteVO> rows ) {
      _keys.clear();
      _map.clear();

      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         SiteVO vo = rows.get( i );
         String key = vo.getSite();
         ArrayList<String> list = vo.getFields();
         
         _keys.add( key );
         _map.put( key, list );
      }
   }
   

   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final ArrayList<String> getSites( String key ) {
      return _map.get( key );
   }
   
   
   public final String getSitesString( String key ) {
      Iterator<String> it = _map.get( key ).iterator();
      String string = it.hasNext() ? it.next() : "";
      
      while ( it.hasNext() ) string += ", " + it.next(); // HARD-CODED delimiter

      return string;
   }

   
   public DatabaseWriteVO getDatabaseWriteVO( int selected, String value ) {
      String id = 0 <= selected && selected < size() ? get( selected ).getSite() : null;
      
      if ( id != null && value == null ) return new DeleteVO( ApplicationFacade.SITES_CHANGED, "sites", "site = '" + id + "'" );

      InsertVO ivo = null;
      ArrayList<String> cols = new ArrayList<String>();
      
      if ( id != null ) {
         cols.add( "fields" );
         ivo = new UpdateVO( ApplicationFacade.SITES_CHANGED, "sites", cols, "site = '" + id + "'" );
         ivo.put( "fields", value );
      } else {      
         cols.add( "site" );
         ivo = new InsertVO( ApplicationFacade.SITES_CHANGED, "sites", cols );
         ivo.put( "site", value );
      }      
      
      return ivo;
   }
}

