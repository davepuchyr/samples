// $Id: PopulateTemplatesCommand.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.TemplatesProxy;
import com.netmorpher.client.model.vo.TemplateVO;

/**
 * @author dave
 *
 */
public class PopulateTemplatesCommand extends DatabaseReadCommand<TemplateVO> {
   @Override
   protected String getSQL( INotification notification ) {
      return "SELECT DISTINCT template FROM templates ORDER BY template";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.TEMPLATES_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return TemplatesProxy.NAME;
   }
   
   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      TemplatesProxy proxy = (TemplatesProxy) getFacade().retrieveProxy( getProxyName() );

      proxy.map( (JsArray<TemplateVO>) rows );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
