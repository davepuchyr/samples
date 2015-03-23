// $Id: TemplateProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.puremvc.java.multicore.patterns.facade.Facade;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.DatabaseWriteVO;
import com.netmorpher.client.model.vo.DeleteVO;
import com.netmorpher.client.model.vo.InsertVO;
import com.netmorpher.client.model.vo.TemplateVO;
import com.netmorpher.client.model.vo.UpdateVO;
import com.netmorpher.client.view.TemplatesMediator;


/**
 * @author dave
 *
 */
public class TemplateProxy extends DatabaseProxy<TemplateVO> {
   public static final String NAME = "TemplateProxy";

   private ArrayList<String> _keys = new ArrayList<String>();
   private HashMap<String, String> _map = new HashMap<String, String>();


   public TemplateProxy() {
      super( NAME );
   }

   
   public void map( JsArray<TemplateVO> rows ) {
      _keys.clear();
      _map.clear();

      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         TemplateVO vo = (TemplateVO) rows.get( i );
         String key = vo.getField(), value = vo.getValue();
         
         _keys.add( key );
         _map.put( key, value );
      }
   }


   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final String getValue( String key ) {
      return _map.get( key );
   }
   
   
   public DatabaseWriteVO getDatabaseWriteVO( int selected, String value ) {
      String id = 0 <= selected && selected < size() ? get( selected ).getField() : null;
      Facade facade = getFacade();
      TemplatesProxy proxy = (TemplatesProxy) facade.retrieveProxy( TemplatesProxy.NAME );
      TemplatesMediator mediator = (TemplatesMediator) facade.retrieveMediator( TemplatesMediator.NAME );
      String template = proxy.get( mediator.getSelected() ).getTemplate();
      String clause = "template = '" + template + "' AND field = '" + id + "'";

      if ( id != null && value == null ) return new DeleteVO( ApplicationFacade.TEMPLATE_CHANGED, "templates", clause );

      InsertVO ivo = null;
      ArrayList<String> cols = new ArrayList<String>();
      
      if ( id != null ) {
         cols.add( "value" );
         ivo = new UpdateVO( ApplicationFacade.TEMPLATE_CHANGED, "templates", cols, clause );
         ivo.put( "value", value );
      } else {      
         cols.add( "template" );
         cols.add( "field" );
         ivo = new InsertVO( ApplicationFacade.TEMPLATE_CHANGED, "templates", cols );
         ivo.put( "field", value );
         ivo.put( "template", template );
      }
      
      return ivo;
   }
}

