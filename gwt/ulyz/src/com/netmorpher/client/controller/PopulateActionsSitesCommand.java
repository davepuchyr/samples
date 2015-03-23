// $Id: PopulateActionsSitesCommand.java 99 2010-05-14 12:39:39Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;

import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.ActionsSitesProxy;
import com.netmorpher.client.model.vo.SiteVO;

/**
 * @author dave
 *
 */
public class PopulateActionsSitesCommand extends DatabaseReadCommand<SiteVO> {
   @Override
   protected String getSQL( final INotification notification ) {
      return "SELECT site, driver, fields FROM sites WHERE driver != '' ORDER BY site";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.ACTIONS_SITES_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return ActionsSitesProxy.NAME;
   }
}
