// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.lib.console.client.Console;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @see <a href=http://stackoverflow.com/questions/744137/best-way-to-externalize-html-in-gwt-apps>Futzilogik's idea.</a>
 */
public interface HelpBundle extends ClientBundle {
   static public final HelpBundle INSTANCE = GWT.create( HelpBundle.class );

   @Source( "help/D3.html" ) public TextResource getD3Page();

   @Source( "help/Home.html" ) public TextResource getHomePage();

   @Source( "help/Missing.html" ) public TextResource getMissingHelpPage();

   @Source( "help/PhoneGap.html" ) public TextResource getPhoneGapPage();


   public class Helper {
      static public String getBody( String token ) {
         String html;

         if ( token.equals( PlaceHistoryMapper.PREFIX_D3 ) ) {
            html = INSTANCE.getD3Page().getText();
         } else if ( token.equals( PlaceHistoryMapper.PREFIX_HOME ) ) {
            html = INSTANCE.getHomePage().getText();
         } else if ( token.equals( PlaceHistoryMapper.PREFIX_PHONEGAP ) ) {
            html = INSTANCE.getPhoneGapPage().getText();
         } else {
            html = INSTANCE.getMissingHelpPage().getText();

            Console.error( "Missing help page for token '" + token + "'; check HelpBundle.java" );
         }

         return html.substring( 0, html.lastIndexOf( "</BODY>" ) ).substring( html.indexOf( "<BODY>" ) + 6 ); // HARD-CODED
      }
   }
}
