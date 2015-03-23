// $Id$
package com.buyside.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.OS;
import org.junit.Test;

/**
 * @author dave
 */
public class BufferedExecutorTest {
   @Test
   public void testDir() throws ExecuteException, IOException {
      CommandLine cmdLine = null;
      
      if ( OS.isFamilyWindows() ) {
     	 cmdLine = new CommandLine( "cmd.exe" );
         cmdLine.addArgument( "/c" );
         cmdLine.addArgument( "dir" );
      } else if ( OS.isFamilyUnix() ) {
    	 cmdLine = new CommandLine( "dir" );
         cmdLine.addArgument( "-s" );
      } else {
         fail( "expected operating system Windows or *nix" );
      }
      
      ExecuteWatchdog watchdog = new ExecuteWatchdog( 3 * 1000 );
      BufferedExecutor executor = new BufferedExecutor();
      executor.setWatchdog( watchdog );
      
      assertTrue( executor.execute( cmdLine ) == 0 );
      assertTrue( executor.getOut().size() > 0 );
      assertTrue( executor.getErr().size() == 0 );
   }
   

   @Test
   public void testPerl() throws ExecuteException, IOException {
      if ( OS.isFamilyWindows() ) return; // short-circuit; this test is for linux only
      
      String exe = "testBufferedExecutor.pl";
      String ud = System.getProperty( "user.dir" );
      exe = ud.indexOf( "util" ) == -1 ? "./util/" + exe : "./" + exe; // cope with build in both util and Applications directory
      String arg0 = "arg0";
      CommandLine cmdLine = new CommandLine( exe );
      cmdLine.addArgument( arg0 );
      ExecuteWatchdog watchdog = new ExecuteWatchdog( 3 * 1000 );
      BufferedExecutor executor = new BufferedExecutor();
      executor.setWatchdog( watchdog );
      
      assertTrue( executor.execute( cmdLine ) == 0 );
      String[] lines = executor.getOut().toString( "UTF-8" ).split( "\\r?\\n" );
      assertTrue( lines.length > 0 );
      assertTrue( lines[0].indexOf( arg0 ) > -1 );
      lines = executor.getErr().toString( "UTF-8" ).split( "\\r?\\n" );
      assertTrue( lines.length > 0 );
      assertTrue( lines[0].indexOf( exe ) > -1 );
   }
}
