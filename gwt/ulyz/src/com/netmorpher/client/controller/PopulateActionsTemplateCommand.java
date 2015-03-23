// $Id: PopulateActionsTemplateCommand.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.ActionsTemplateProxy;
import com.netmorpher.client.model.vo.TemplateVO;
import com.netmorpher.client.view.ActionsMediator;

/**
 * @author dave
 *
 */
public class PopulateActionsTemplateCommand extends DatabaseReadCommand<TemplateVO> {
   @Override
   protected String getSQL( INotification notification ) {
      ActionsMediator mediator = (ActionsMediator) getFacade().retrieveMediator( ActionsMediator.NAME );

      return "SELECT field, value FROM templates WHERE template = '" + mediator.getTemplateString() + "'";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.ACTIONS_TEMPLATE_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return ActionsTemplateProxy.NAME;
   }

   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      ActionsTemplateProxy proxy = (ActionsTemplateProxy) getFacade().retrieveProxy( ActionsTemplateProxy.NAME );

      proxy.map( (JsArray<TemplateVO>) rows );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
