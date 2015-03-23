// $Id$
package com.avaritia.lib.java.fsm.client;

import com.avaritia.lib.console.client.Console;
import com.avaritia.lib.js.sprintf.client.Sprintf;


public class LoggerImpl implements com.avaritia.lib.java.fsm.shared.Logger {
   private String replace( String format, Object... objects ) {
      for ( Object o : objects ) {
         format = format.replaceFirst( "\\{\\}", o.toString().replace( "$", "_" ).replace( "\\", "_" ) ); // HACK around match group refs in dev mode
      }

      return format;
   }


   @Override public void debug( String format, Object... objects ) {
      Console.debug(  replace( format, objects ) );
   }


   @Override public void error( String format, Object... objects ) {
      Console.error( replace( format, objects ) );
   }


   @Override public void info( String format, Object... objects ) {
      Console.info( replace( format, objects ) );
   }


   @Override public void trace( String format, Object... objects ) {
      Console.trace( replace( format, objects ) );
   }


   @Override public String sprintf( String format, Object... objects ) {
      int i = 0, n = objects.length;
      Object[] strings = new Object[n];

      for ( ; i < n; ++i ) {
         strings[i] = objects[i] == null ? "null" : objects[i].toString();
      }

      return Sprintf.format( format, strings );
   }


   @Override public void warn( String format, Object... objects ) {
      Console.warn( replace( format, objects ) );
   }
}
