// $Id: PopulateAccountsCommand.java 34 2009-12-19 18:42:32Z dave $
package com.netmorpher.client.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;
import org.puremvc.java.multicore.patterns.facade.Facade;

import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.FungiblesProxy;
import com.netmorpher.client.model.MappingsProxy;
import com.netmorpher.client.model.SynonymsProxy;
import com.netmorpher.client.model.ActionsTemplateProxy;
import com.netmorpher.client.view.ActionsMediator;

/**
 * @author dave
 *
 */
public class PopulateFungiblesCommand extends SimpleCommand {
   public void execute( final INotification notification ) {
      Facade facade = getFacade();
      MappingsProxy proxyMappings = (MappingsProxy) facade.retrieveProxy( MappingsProxy.NAME );
      
      if ( proxyMappings.getKeys() == null ) return; // short-circuit since we don't have either site or template selected
      
      ActionsMediator mediatorActions = (ActionsMediator) facade.retrieveMediator( ActionsMediator.NAME );
      ActionsTemplateProxy proxyTemplate = (ActionsTemplateProxy) facade.retrieveProxy( ActionsTemplateProxy.NAME );
      SynonymsProxy proxySynonyms = (SynonymsProxy) facade.retrieveProxy( SynonymsProxy.NAME );
      FungiblesProxy proxyFungibles = (FungiblesProxy) facade.retrieveProxy( FungiblesProxy.NAME );
      
      HashMap<String, String> lazy = new HashMap<String, String>();
      lazy.put( "{{__site__}}", mediatorActions.getSiteString() ); // HARD-CODED key
      lazy.put( "{{__account__}}", mediatorActions.getAccountString() ); // HARD-CODED key
      lazy.put( "{{__template__}}", mediatorActions.getTemplateString() ); // HARD-CODED key
      
      for ( String key : proxyTemplate.getKeys() ) { // template constants
         if ( key.indexOf( "{{" ) != 0 || key.indexOf( "}}" ) != key.length() - 2 ) continue; // HARD-CODED delimiters
         
         lazy.put( key, proxyTemplate.getValue( key ) );
      }
      
      for ( String key : proxyMappings.getKeys() ) { // template fields pertaining to the selected site
         lazy.put( key, proxyMappings.getValue( key ) );
      }
      
      HashMap<String, ArrayList<String>> oneToMany = new HashMap<String, ArrayList<String>>();
      
      for ( String key : lazy.keySet() ) oneToMany.put( key, new ArrayList<String>( Arrays.asList( new String[] { lazy.get( key ) } ) ) );
   
      proxyFungibles.update( oneToMany, proxySynonyms );
      
      facade.sendNotification( ApplicationFacade.FUNGIBLES_UPDATED );
   }
}
