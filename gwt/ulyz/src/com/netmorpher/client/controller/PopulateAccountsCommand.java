// $Id: PopulateAccountsCommand.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.facade.Facade;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.AccountsProxy;
import com.netmorpher.client.model.SitesProxy;
import com.netmorpher.client.model.vo.AccountVO;
import com.netmorpher.client.view.SitesMediator;

/**
 * @author dave
 *
 */
public class PopulateAccountsCommand extends DatabaseReadCommand<AccountVO> {
   @Override
   protected String getSQL( final INotification notification ) {
      Facade facade = getFacade();
      SitesProxy proxy = (SitesProxy) facade.retrieveProxy( SitesProxy.NAME );
      SitesMediator mediator = (SitesMediator) facade.retrieveMediator( SitesMediator.NAME );
      String site = proxy.get( mediator.getSelected() ).getSite();

      return "SELECT site, user, password FROM accounts WHERE site = '" + site + "' ORDER BY user";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.ACCOUNTS_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return AccountsProxy.NAME;
   }

   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      AccountsProxy proxy = (AccountsProxy) getFacade().retrieveProxy( AccountsProxy.NAME );

      proxy.map( (JsArray<AccountVO>) rows );
      
      super.onSuccess( rows ); // sends notification so must be after specialized onSuccess
   }
}
