// $Id: PopulateTemplateCommand.java 103 2010-05-17 13:44:21Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.facade.Facade;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.TemplateProxy;
import com.netmorpher.client.model.TemplatesProxy;
import com.netmorpher.client.model.vo.TemplateVO;
import com.netmorpher.client.view.TemplatesMediator;

/**
 * @author dave
 *
 */
public class PopulateTemplateCommand extends DatabaseReadCommand<TemplateVO> {
   @Override
   protected String getSQL( INotification notification ) {
      Facade facade = getFacade();
      TemplatesProxy proxy = (TemplatesProxy) facade.retrieveProxy( TemplatesProxy.NAME );
      TemplatesMediator mediator = (TemplatesMediator) facade.retrieveMediator( TemplatesMediator.NAME );
      String template = proxy.get( mediator.getSelected() ).getTemplate();

      return "SELECT field, value FROM templates WHERE template = '" + template + "' ORDER BY field";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.TEMPLATE_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return TemplateProxy.NAME;
   }

   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      TemplateProxy proxy = (TemplateProxy) getFacade().retrieveProxy( TemplateProxy.NAME );

      proxy.map( (JsArray<TemplateVO>) rows );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
