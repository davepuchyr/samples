// $Id$
package com.avaritia.app.in3s.rebind;

import com.avaritia.lib.js.jsni.rebind.JsniBundleGenerator;
import com.google.gwt.core.ext.TreeLogger;

/**
 * Overrides {@link JsniBundleGenerator#filterContent(String)} so that showcase.js can be bundled.
 */
public class AppBundleGenerator extends JsniBundleGenerator {
   @Override public String filterContent( TreeLogger logger, String js ) {
      String snippet = js.substring( 0, 20 ) + "..." + js.substring( js.length() - 20 );

      snippet = snippet.replace( "\n", " " );

      logger.log( TreeLogger.INFO, getClass().getSimpleName() + " filterContent() adding '" + snippet + "' to $wnd" );

      return "( function( window, document, console ) { " + js + " window.app = app; } ).apply( $wnd, [ $wnd, $doc, $wnd.console ] );";
   }
}

