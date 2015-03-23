// $Id: PopulateAccountsCommand.java 34 2009-12-19 18:42:32Z dave $
package com.netmorpher.client.controller;

import java.util.ArrayList;
import java.util.Iterator;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.facade.Facade;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.ActionsSitesProxy;
import com.netmorpher.client.model.MappingsProxy;
import com.netmorpher.client.model.vo.MappingVO;
import com.netmorpher.client.view.ActionsMediator;

/**
 * @author dave
 *
 */
public class PopulateMappingsCommand extends DatabaseReadCommand<MappingVO> {
   private ArrayList<String> _fields = null;


   @Override
   protected String getSQL( final INotification notification ) {
      Facade facade = getFacade();
      ActionsMediator mediatorActions = (ActionsMediator) facade.retrieveMediator( ActionsMediator.NAME );
      
      if ( mediatorActions.getSiteString().isEmpty() || mediatorActions.getTemplateString().isEmpty() ) return null;
      
      ActionsSitesProxy proxySites = (ActionsSitesProxy) facade.retrieveProxy( ActionsSitesProxy.NAME );
      _fields = proxySites.get( mediatorActions.getSite() ).getFields();
      
      if ( _fields.size() == 0 ) return null;
      
      Iterator<String> it = _fields.iterator();
      String fields = it.hasNext() ? it.next() : "";
      
      while ( it.hasNext() ) fields += "', '" + it.next();
      
      return "SELECT field, value FROM templates " +
      		 "WHERE template = '" + mediatorActions.getTemplateString() + "' " +
      		 "AND field in ( '" + fields + "' )";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.MAPPINGS_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return MappingsProxy.NAME;
   }


   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      ApplicationFacade facade = ApplicationFacade.getInstance();
      MappingsProxy proxy = (MappingsProxy) facade.retrieveProxy( getProxyName() );

      proxy.map( (JsArray<MappingVO>) rows, _fields );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
