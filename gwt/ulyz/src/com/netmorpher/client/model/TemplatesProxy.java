// $Id: TemplatesProxy.java 101 2010-05-14 20:33:06Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.DatabaseWriteVO;
import com.netmorpher.client.model.vo.DeleteVO;
import com.netmorpher.client.model.vo.InsertVO;
import com.netmorpher.client.model.vo.TemplateVO;
import com.netmorpher.client.model.vo.UpdateVO;

/**
 * @author dave
 *
 */
public class TemplatesProxy extends DatabaseProxy<TemplateVO> {
   public static final String NAME = "TemplatesProxy";

   private ArrayList<String> _keys = new ArrayList<String>();

   
   public TemplatesProxy() {
      super( NAME );
   }
   
   
   public void map( JsArray<TemplateVO> rows ) {
      _keys.clear();

      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         _keys.add( rows.get( i ).getTemplate() );
      }
   }
   

   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public DatabaseWriteVO getDatabaseWriteVO( int selected, String template ) {
      String id = 0 <= selected && selected < size() ? get( selected ).getTemplate() : null;
      String clause = "template = '" + id + "'";
      
      if ( id != null && template == null ) return new DeleteVO( ApplicationFacade.TEMPLATES_CHANGED, "templates", clause );

      InsertVO ivo = null;
      ArrayList<String> cols = new ArrayList<String>();
      cols.add( "template" );
      
      if ( id != null ) {
         ivo = new UpdateVO( ApplicationFacade.TEMPLATES_CHANGED, "templates", cols, clause );
      } else {
         ivo = new InsertVO( ApplicationFacade.TEMPLATES_CHANGED, "templates", cols );
      }
      
      ivo.put( "template", template );
      
      return ivo;
   }
}

