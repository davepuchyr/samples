// $Id$
package com.avaritia.lib.java.fsm.server;

import org.slf4j.LoggerFactory;
import com.avaritia.lib.java.fsm.shared.Logger;


public class LoggerImpl implements Logger {
   final static private org.slf4j.Logger LOGGER = LoggerFactory.getLogger( LoggerImpl.class );


   @Override public void debug( String format, Object... objects ) {
      LOGGER.debug( format, objects );
   }


   @Override public void error( String format, Object... objects ) {
      LOGGER.error( format, objects );
   }


   @Override public void info( String format, Object... objects ) {
      LOGGER.info( format, objects );
   }


   @Override public void trace( String format, Object... objects ) {
      LOGGER.trace( format, objects );
   }


   @Override public String sprintf( String format, Object... objects ) {
      return String.format( format, objects );
   }


   @Override public void warn( String format, Object... objects ) {
      LOGGER.warn( format, objects );
   }
}
