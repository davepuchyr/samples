// $Id: PopulateSitesCommand.java 98 2010-05-14 12:04:32Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.SitesProxy;
import com.netmorpher.client.model.vo.SiteVO;

/**
 * @author dave
 *
 */
public class PopulateSitesCommand extends DatabaseReadCommand<SiteVO> {
   @Override
   protected String getSQL( final INotification notification ) {
      return "SELECT site, fields, driver FROM sites ORDER BY site";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.SITES_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return SitesProxy.NAME;
   }
   
   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      SitesProxy proxy = (SitesProxy) getFacade().retrieveProxy( getProxyName() );

      proxy.map( (JsArray<SiteVO>) rows );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
