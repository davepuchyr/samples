// $Id: SchedulerFactoryService.java 1019 2013-04-15 21:10:20Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.spi.JobFactory;
import com.google.inject.Inject;

/**
 * Provides {@link Scheduler}s for a given {@link SchedulerFactory} instance name. Subclasses must implement the addFactory method
 * and put instances into the instance2factory member.
 */
public abstract class SchedulerFactoryService {
   @Inject JobFactory jobFactory;

   protected Map<String, SchedulerFactory> instance2factory = new HashMap<String, SchedulerFactory>();


   abstract public void addFactory( String name, Properties properties ) throws SchedulerException;


   /**
    * Provides and starts a {@link Scheduler} for key if the {@link Scheduler} instance exists and has not been shutdown.
    * 
    * @param key
    * @return a Scheduler or null
    * @throws SchedulerException
    */
   public Scheduler getScheduler( String key ) throws SchedulerException {
      Scheduler scheduler = instance2factory.containsKey( key ) ? instance2factory.get( key ).getScheduler() : null;
      
      if ( scheduler == null ) throw new SchedulerException( "No factory for instance " + key );
      
      if ( !scheduler.isStarted() ) {
         scheduler.setJobFactory( jobFactory );
         scheduler.start();
      } else if ( scheduler.isShutdown() ) {
         throw new SchedulerException( "Scheduler " + key + " is shutdown" );
      }

      return scheduler;
   }
}
