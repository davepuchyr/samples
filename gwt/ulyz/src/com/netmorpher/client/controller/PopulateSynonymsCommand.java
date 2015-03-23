// $Id: PopulateSynonymsCommand.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.controller;

import java.util.ArrayList;

import org.puremvc.java.multicore.interfaces.INotification;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.SynonymsProxy;
import com.netmorpher.client.model.vo.SynonymVO;

/**
 * @author dave
 *
 */
public class PopulateSynonymsCommand extends DatabaseReadCommand<SynonymVO> {
   private ArrayList<String> _synonyms = null;

   
   @Override
   protected String getSQL( INotification notification ) {
      /*
      TemplateProxy proxy = (TemplateProxy) getFacade().retrieveProxy( TemplateProxy.NAME );
      
      _synonyms = proxy.getKeywords();

      Iterator<String> it = _synonyms.iterator();
      String clause = it.hasNext() ? it.next() : "";
      
      while ( it.hasNext() ) clause += "', '" + it.next();

      return "SELECT * FROM keywords WHERE keyword in ( '" + clause + "' )";
      */
      return "SELECT * FROM keywords ORDER BY keyword"; // TODO: be smart when the number of synonyms gets out of control
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.SYNONYMS_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return SynonymsProxy.NAME;
   }
   
   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      SynonymsProxy proxy = (SynonymsProxy) getFacade().retrieveProxy( getProxyName() );

      proxy.map( (JsArray<SynonymVO>) rows, _synonyms );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
