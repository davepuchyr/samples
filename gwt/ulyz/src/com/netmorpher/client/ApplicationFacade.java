// $Id: ApplicationFacade.java 100 2010-05-14 12:59:15Z dave $
package com.netmorpher.client;


import org.puremvc.java.multicore.patterns.command.MacroCommand;
import org.puremvc.java.multicore.patterns.facade.Facade;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.netmorpher.client.controller.DatabaseWriteCommand;
import com.netmorpher.client.controller.PopulateAccountsCommand;
import com.netmorpher.client.controller.PopulateActionsAccountsCommand;
import com.netmorpher.client.controller.PopulateActionsSitesCommand;
import com.netmorpher.client.controller.PopulateActionsTemplatesCommand;
import com.netmorpher.client.controller.PopulateFungiblesCommand;
import com.netmorpher.client.controller.PopulateMappingsCommand;
import com.netmorpher.client.controller.PopulateSitesCommand;
import com.netmorpher.client.controller.PopulateSynonymsCommand;
import com.netmorpher.client.controller.PopulateActionsTemplateCommand;
import com.netmorpher.client.controller.PopulateTemplateCommand;
import com.netmorpher.client.controller.PopulateTemplatesCommand;
import com.netmorpher.client.controller.PublishCommand;
import com.netmorpher.client.controller.StartupCommand;
import com.netmorpher.client.controller.SubstituteKeywordsCommand;
import com.netmorpher.client.view.ActionsMediator;
import com.netmorpher.client.view.SitesAccountsMediator;
import com.netmorpher.client.view.SynonymsMediator;
import com.netmorpher.client.view.TemplatesTemplateMediator;

/**
 * @author dave
 *
 */
public class ApplicationFacade extends Facade implements ResizeHandler {
   public static final String NAME = "ApplicationFacade";
   
   public static final String STARTUP = "STARTUP";
   public static final String INITIALIZE = "INITIALIZE";

   public static final String ON_RESIZE = "ON_RESIZE";

   public static final String ACCOUNT_SELECTED = "ACCOUNT_SELECTED";
   public static final String ACCOUNTS_CHANGED = "ACCOUNTS_CHANGED";
   public static final String ACCOUNTS_UPDATED = "ACCOUNTS_UPDATED";
   public static final String ACTIONS_ACCOUNT_SELECTED = "ACTIONS_ACCOUNT_SELECTED";
   public static final String ACTIONS_ACCOUNTS_UPDATED = "ACTIONS_ACCOUNTS_UPDATED";
   public static final String ACTIONS_SITE_SELECTED = "ACTIONS_SITE_SELECTED";
   public static final String ACTIONS_SITES_CHANGED = "ACTIONS_SITES_CHANGED";
   public static final String ACTIONS_SITES_UPDATED = "ACTIONS_SITES_UPDATED";
   public static final String ACTIONS_TEMPLATE_SELECTED = "ACTIONS_TEMPLATE_SELECTED";
   public static final String ACTIONS_TEMPLATE_UPDATED = "ACTIONS_TEMPLATE_UPDATED";
   public static final String ACTIONS_TEMPLATES_UPDATED = "ACTIONS_TEMPLATES_UPDATED";
   public static final String DATABASE_WRITE = "DATABASE_WRITE";
   public static final String DIALOG_HIDE = "DIALOG_HIDE";
   public static final String DIALOG_SHOW = "DIALOG_SHOW";
   public static final String FUNGIBLES_UPDATED = "FUNGIBLES_UPDATED";
   public static final String MAPPING_SELECTED = "MAPPING_SELECTED";
   public static final String MAPPINGS_UPDATED = "MAPPINGS_UPDATED";
   public static final String PUBLISH = "PUBLISH";
   public static final String SHOW_TEMPLATE = "SHOW_TEMPLATE";
   public static final String SITE_SELECTED = "SITE_SELECTED";
   public static final String SITES_CHANGED = "SITES_CHANGED";
   public static final String SITES_UPDATED = "SITES_UPDATED";
   public static final String SNAPSHOT_UPDATED = "SNAPSHOT_UPDATED";
   public static final String SUBSTITUTE_KEYWORDS = "SUBSTITUTE_KEYWORDS";
   public static final String SYNONYMS_CHANGED = "SYNONYMS_CHANGED";
   public static final String SYNONYMS_UPDATED = "SYNONYMS_UPDATED";
   public static final String TEMPLATE_CHANGED = "TEMPLATE_CHANGED";
   public static final String TEMPLATE_SELECTED = "TEMPLATE_SELECTED";
   public static final String TEMPLATE_UPDATED = "TEMPLATE_UPDATED";
   public static final String TEMPLATES_CHANGED = "TEMPLATES_CHANGED";
   public static final String TEMPLATES_UPDATED = "TEMPLATES_UPDATED";

   private static ApplicationFacade _instance = null;
   private static TabPanel _root = new TabPanel();

   
   protected ApplicationFacade() {
      super( NAME );
   }


   public static ApplicationFacade getInstance() {
      if ( _instance == null ) {
         _instance = new ApplicationFacade();
         _instance.sendNotification( STARTUP );
         _instance.sendNotification( INITIALIZE );

         // read from database
         _instance.sendNotification( SITES_CHANGED );
         _instance.sendNotification( TEMPLATES_CHANGED );
         _instance.sendNotification( SYNONYMS_CHANGED );
         _instance.sendNotification( ACTIONS_SITES_CHANGED );
         
         _root.add( (Widget) _instance.retrieveMediator( SitesAccountsMediator.NAME ).getViewComponent(), "Accounts" );
         _root.add( (Widget) _instance.retrieveMediator( TemplatesTemplateMediator.NAME ).getViewComponent(), "Templates" );
         _root.add( (Widget) _instance.retrieveMediator( SynonymsMediator.NAME ).getViewComponent(), "Keywords" );
         _root.add( (Widget) _instance.retrieveMediator( ActionsMediator.NAME ).getViewComponent(), "Actions" );
         
         _root.selectTab( 0 );
      }

      return _instance;
   }

   
   protected void initializeController() {
      super.initializeController();

      registerCommand( STARTUP, new StartupCommand() );
      registerCommand( ACCOUNTS_CHANGED, new PopulateAccountsCommand() );
      registerCommand( ACCOUNTS_UPDATED, new PopulateActionsAccountsCommand() );
      registerCommand( ACTIONS_ACCOUNT_SELECTED, new PopulateFungiblesCommand() );
      registerCommand( ACTIONS_SITE_SELECTED, new MacroCommand() {
         protected void initializeMacroCommand() {
            addSubCommand( new PopulateActionsAccountsCommand() );
            addSubCommand( new PopulateMappingsCommand() );
         }
      } );
      registerCommand( ACTIONS_TEMPLATE_SELECTED, new PopulateActionsTemplateCommand() );
      registerCommand( ACTIONS_TEMPLATE_UPDATED, new PopulateMappingsCommand() );
      registerCommand( ACTIONS_SITES_CHANGED, new PopulateActionsSitesCommand() );
      registerCommand( DATABASE_WRITE, new DatabaseWriteCommand() );
      registerCommand( MAPPINGS_UPDATED, new PopulateFungiblesCommand() );
      registerCommand( PUBLISH, new PublishCommand() );
      registerCommand( TEMPLATE_CHANGED, new PopulateTemplateCommand() );
      registerCommand( TEMPLATE_SELECTED, new PopulateTemplateCommand() );
      registerCommand( TEMPLATE_UPDATED, new PopulateMappingsCommand() );
      registerCommand( TEMPLATES_CHANGED, new MacroCommand() {
         protected void initializeMacroCommand() {
            addSubCommand( new PopulateActionsTemplatesCommand() );
            addSubCommand( new PopulateTemplatesCommand() );
         }
      } );
      registerCommand( SITE_SELECTED, new PopulateAccountsCommand() );
      registerCommand( SITES_CHANGED, new PopulateSitesCommand() );
      registerCommand( SUBSTITUTE_KEYWORDS, new SubstituteKeywordsCommand() );
      registerCommand( SYNONYMS_CHANGED, new PopulateSynonymsCommand() );
      registerCommand( SYNONYMS_UPDATED,new PopulateFungiblesCommand() );
   }


   public Widget getWidget() {
      return _root;
   }


   public void onResize( ResizeEvent event ) {
      _root.setWidth( "100%" );
      /*
      int h = _root.getOffsetHeight();
            int h = Main.heightWindow = Window.getClientHeight();
      int w = Main.widthWindow = Window.getClientWidth();

      sendNotification( ApplicationFacade.ON_RESIZE, event );
      */
   }
}
