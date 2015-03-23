// $Id: PopulateActionsTemplatesCommand.java 101 2010-05-14 20:33:06Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;

import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.ActionsTemplatesProxy;
import com.netmorpher.client.model.vo.TemplateVO;

/**
 * @author dave
 *
 */
public class PopulateActionsTemplatesCommand extends DatabaseReadCommand<TemplateVO> {
   @Override
   protected String getSQL( INotification notification ) {
      return "SELECT template FROM templates GROUP BY template HAVING COUNT(*) > 1 ORDER BY template";
   }


   @Override
   protected String getNotificationName() {
      return ApplicationFacade.ACTIONS_TEMPLATES_UPDATED;
   }


   @Override
   protected String getProxyName() {
      return ActionsTemplatesProxy.NAME;
   }
}
