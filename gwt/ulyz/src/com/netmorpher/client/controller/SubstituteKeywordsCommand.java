// $Id: SubstituteKeywordsCommand.java 98 2010-05-14 12:04:32Z dave $
package com.netmorpher.client.controller;

import java.util.HashMap;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.ActionVO;

/**
 * @author dave
 *
 */
public class SubstituteKeywordsCommand extends SimpleCommand {
   public void execute( final INotification notification ) {
      ActionVO vo = (ActionVO) notification.getBody();
      HashMap<String, String> template = vo.getTemplate(); // should really clone a final template returned from getTemplate
      HashMap<String, String> synonyms = vo.getSynonyms();
      boolean dirty = false;

      for ( String synonym : synonyms.keySet() ) {
         for ( String field : synonyms.keySet() ) {
            synonyms.put( field, replaceKeyword( synonyms.get( field ), synonym, synonyms.get( synonym ) ) );
         }
      }

      for ( String synonym : synonyms.keySet() ) {
         for ( String field : template.keySet() ) {
            String value = replaceKeyword( template.get( field ), synonym, synonyms.get( synonym ) );
            template.put( field, value );
         }
      }
      
      for ( String value : template.values() ) {
         dirty |= value.indexOf( "{{" ) != -1; // HARD-CODED delimiter
      }

      getFacade().sendNotification( ApplicationFacade.SNAPSHOT_UPDATED, new ActionVO( template, dirty ) );
   }
   
   
   public final native String replaceKeyword( String template, String key, String value ) /*-{
      key = key.replace( /\{/g, "\\{" ).replace( /\}/g, "\\}" ); // HARD-CODED delimiters
      return template.replace( new RegExp( key, "g" ), value );
   }-*/;
}
