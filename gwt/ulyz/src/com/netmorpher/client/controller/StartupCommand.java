// $Id: StartupCommand.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;
import org.puremvc.java.multicore.patterns.facade.Facade;

import com.netmorpher.client.model.AccountsProxy;
import com.netmorpher.client.model.ActionsAccountsProxy;
import com.netmorpher.client.model.ActionsSitesProxy;
import com.netmorpher.client.model.ActionsTemplatesProxy;
import com.netmorpher.client.model.FungiblesProxy;
import com.netmorpher.client.model.MappingsProxy;
import com.netmorpher.client.model.SitesProxy;
import com.netmorpher.client.model.SynonymsProxy;
import com.netmorpher.client.model.ActionsTemplateProxy;
import com.netmorpher.client.model.TemplateProxy;
import com.netmorpher.client.model.TemplatesProxy;
import com.netmorpher.client.view.AccountsMediator;
import com.netmorpher.client.view.ActionsMediator;
import com.netmorpher.client.view.DialogMediator;
import com.netmorpher.client.view.SitesAccountsMediator;
import com.netmorpher.client.view.SitesMediator;
import com.netmorpher.client.view.SynonymsMediator;
import com.netmorpher.client.view.TemplateMediator;
import com.netmorpher.client.view.TemplatesMediator;
import com.netmorpher.client.view.TemplatesTemplateMediator;

/**
 * @author dave
 *
 */
public class StartupCommand extends SimpleCommand {
   public void execute( final INotification notification ) {
      Facade facade = this.getFacade();
      
      facade.registerProxy( new AccountsProxy() );
      facade.registerProxy( new ActionsAccountsProxy() );
      facade.registerProxy( new ActionsSitesProxy() );
      facade.registerProxy( new ActionsTemplateProxy() );
      facade.registerProxy( new ActionsTemplatesProxy() );
      facade.registerProxy( new FungiblesProxy() );
      facade.registerProxy( new MappingsProxy() );
      facade.registerProxy( new SitesProxy() );
      facade.registerProxy( new SynonymsProxy() );
      facade.registerProxy( new TemplateProxy() );
      facade.registerProxy( new TemplatesProxy() );
   
      facade.registerMediator( new AccountsMediator() );
      facade.registerMediator( new ActionsMediator() );
      facade.registerMediator( new DialogMediator() );
      facade.registerMediator( new SitesAccountsMediator() );
      facade.registerMediator( new SitesMediator() );
      facade.registerMediator( new SynonymsMediator() );
      facade.registerMediator( new TemplatesTemplateMediator() );
      facade.registerMediator( new TemplatesMediator() );
      facade.registerMediator( new TemplateMediator() );
   }
}
