// $Id: PopulateActionsAccountsCommand.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;

import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.ActionsAccountsProxy;
import com.netmorpher.client.model.vo.AccountVO;
import com.netmorpher.client.view.ActionsMediator;

/**
 * @author dave
 *
 */
public class PopulateActionsAccountsCommand extends DatabaseReadCommand<AccountVO> {
   @Override
   protected String getSQL( final INotification notification ) {
      ActionsMediator mediator = (ActionsMediator) getFacade().retrieveMediator( ActionsMediator.NAME );

      return "SELECT user FROM accounts WHERE site = '" + mediator.getSiteString() + "' ORDER BY user";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.ACTIONS_ACCOUNTS_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return ActionsAccountsProxy.NAME;
   }
}
