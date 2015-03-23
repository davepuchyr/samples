// $Id: SynonymsProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.DatabaseWriteVO;
import com.netmorpher.client.model.vo.DeleteVO;
import com.netmorpher.client.model.vo.InsertVO;
import com.netmorpher.client.model.vo.SynonymVO;
import com.netmorpher.client.model.vo.UpdateVO;

/**
 * @author dave
 *
 */
public class SynonymsProxy extends DatabaseProxy<SynonymVO> {
   public static final String NAME = "SynonymsProxy";
   
   private ArrayList<String> _keys = new ArrayList<String>();
   private HashMap<String, ArrayList<String>> _map = new HashMap<String, ArrayList<String>>();


   public SynonymsProxy() {
      super( NAME );
   }
   
   
   public void map( JsArray<SynonymVO> rows, ArrayList<String> synonyms ) {
      //_keys = synonyms; // TODO: be smart when the number of synonyms gets out of control
      _keys.clear();
      _map.clear();

      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         SynonymVO vo = rows.get( i );
         String key = vo.getKeyword();
         String[] existing = vo.getSynonyms().split( "," ); // HARD-CODED delimiter
         ArrayList<String> list = new ArrayList<String>();
         
         _keys.add( key );
         _map.put( key, list );
         
         for ( int j = 0, m = existing.length; j < m; ++j ) {
            list.add( existing[j].replace( "^\\s+", "" ).replace( "\\s+$", "" ) );
         }
      }
   }
   

   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final ArrayList<String> getSynonyms( String key ) {
      return _map.get( key );
   }
   
   
   public final String getSynonymsString( String key ) {
      Iterator<String> it = _map.get( key ).iterator();
      String synonyms = it.hasNext() ? it.next() : "";
      
      while ( it.hasNext() ) synonyms += ", " + it.next(); // HARD-CODED delimiter

      return synonyms;
   }

   
   public DatabaseWriteVO getDatabaseWriteVO( int selected, String value ) {
      String id = 0 <= selected && selected < size() ? get( selected ).getKeyword() : null;
      
      if ( id != null && value == null ) return new DeleteVO( ApplicationFacade.SYNONYMS_CHANGED, "keywords", "keyword = '" + id + "'" );

      InsertVO ivo = null;
      ArrayList<String> cols = new ArrayList<String>();
      
      if ( id != null ) {
         cols.add( "synonyms" );
         ivo = new UpdateVO( ApplicationFacade.SYNONYMS_CHANGED, "keywords", cols, "keyword = '" + id + "'" );
         ivo.put( "synonyms", value );         
      } else {      
         cols.add( "keyword" );
         ivo = new InsertVO( ApplicationFacade.SYNONYMS_CHANGED, "keywords", cols );
         ivo.put( "keyword", "{{" + value + "}}" ); // HARD-CODED delimiters
      }
      
      return ivo;
   }
}

