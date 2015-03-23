// $Id$
package com.buyside.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * @author dave
 *
 */
public class BufferedExecutor implements Executor {
   protected DefaultExecutor executor;
   
   protected ByteArrayOutputStream os;
   protected ByteArrayOutputStream es;
   
   
   public BufferedExecutor() {
      this( System.in );
   }
   

   public BufferedExecutor( InputStream in ) {
      os = new ByteArrayOutputStream();
      es = new ByteArrayOutputStream();

      executor = new DefaultExecutor();
      executor.setStreamHandler( new PumpStreamHandler( os, es, in  ) );
   }
   
   
   public ByteArrayOutputStream getOut() {
      return os;
   }
   
   
   public ByteArrayOutputStream getErr() {
      return es;
   }
   
   
   @Override public void setExitValue( int value ) {
      executor.setExitValue( value );      
   }

   
   @Override public void setExitValues( int[] values ) {
      executor.setExitValues( values );      
   }

   
   @Override public boolean isFailure( int exitValue ) {
      return executor.isFailure( exitValue );
   }
   

   @Override public ExecuteStreamHandler getStreamHandler() {
      return executor.getStreamHandler();
   }

   
   @Override public void setStreamHandler( ExecuteStreamHandler streamHandler ) {
      // no-op; that's the whole point
   }

   
   @Override public ExecuteWatchdog getWatchdog() {
      return executor.getWatchdog();
   }

   @Override public void setWatchdog( ExecuteWatchdog watchDog ) {
      executor.setWatchdog( watchDog );      
   }

   
   @Override public ProcessDestroyer getProcessDestroyer() {
      return executor.getProcessDestroyer();
   }

   
   @Override public void setProcessDestroyer( ProcessDestroyer processDestroyer ) {
      executor.setProcessDestroyer( processDestroyer );      
   }

   
   @Override public File getWorkingDirectory() {
      return executor.getWorkingDirectory();
   }

   
   @Override public void setWorkingDirectory( File dir ) {
      executor.setWorkingDirectory( dir );
   }

   
   @Override public int execute( CommandLine command ) throws ExecuteException, IOException {
      return executor.execute( command );
   }

   
   @Override public int execute( CommandLine command, @SuppressWarnings( "rawtypes" ) Map environment ) throws ExecuteException, IOException {
      return executor.execute( command, environment );
   }
   

   @Override public void execute( CommandLine command, ExecuteResultHandler handler ) throws ExecuteException, IOException {
      executor.execute( command, handler );
   }

   
   @Override public void execute( CommandLine command, @SuppressWarnings( "rawtypes" ) Map environment, ExecuteResultHandler handler ) throws ExecuteException, IOException {
      executor.execute( command, environment, handler );
   }
}
