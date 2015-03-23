// $Id$
package com.avaritia.lib.java.fsm.shared;


public interface Logger {
   public void debug( String format, Object... objects );

   public void error( String format, Object... objects );

   public void info( String format, Object... objects );

   public void trace( String format, Object... objects );

   public void warn( String format, Object... objects );

   public String sprintf( String format, Object... objects );
}
