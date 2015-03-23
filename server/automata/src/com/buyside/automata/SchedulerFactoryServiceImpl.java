// $Id: SchedulerFactoryServiceImpl.java 1026 2013-04-22 19:00:13Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.buyside.util.Utils;

/**
 * Maps {@link SchedulerFactory} instance names to the factories themselves by parsing {@link AutomataModule}'s properties. @see <a
 * href="../etc/automata.default.properties">../etc/automata.default.properties</a> and @see <a
 * href="http://quartz-scheduler.org/documentation/quartz-2.1.x/configuration/">Quartz' documentation</a> for examples and details.
 * 
 * @author dave
 * 
 */
@Singleton
public class SchedulerFactoryServiceImpl extends SchedulerFactoryService {
   final static private String PREFIX_NA = "com.buyside.automata.schedulerfactory."; // prefix to strip...
   final static private String PREFIX_QUARTZ = "org.quartz."; // ...prefix to prefix
   final static private String LOCALHOST = "localhost";
   final static private String HOSTNAME = Utils.getHostname();
         
   
   @Inject public SchedulerFactoryServiceImpl( AutomataModule automataModule ) throws SchedulerException {
      final int offsetInstance = PREFIX_NA.split( "\\." ).length;
      Properties config = automataModule.getProperties();
      Map<String, Properties> instances = new HashMap<String, Properties>();
      
      for ( Object o : config.keySet() ) {
         String key = o.toString();
         
         if ( key.indexOf( PREFIX_NA ) == 0 ) {
            String[] chunks = key.split( "\\." );
            String instance = chunks[offsetInstance].toLowerCase();
            String qkey = PREFIX_QUARTZ + StringUtils.join( Arrays.copyOfRange( chunks, offsetInstance + 1, chunks.length ), "." );
            
            if ( !instances.containsKey( instance ) ) {
               instances.put( instance, new Properties() ) ;
            }
            
            instances.get( instance ).put( qkey, config.get( o ) );
            //System.out.println( qkey + " -> " + config.get( o ) );
         }
      }
      
      for ( String instance : instances.keySet() ) {
         Properties properties = instances.get( instance );
         String registryHost = properties.containsKey( StdSchedulerFactory.PROP_SCHED_RMI_HOST ) ? properties.get( StdSchedulerFactory.PROP_SCHED_RMI_HOST ).toString() : ""; 
         
         if ( properties.get( StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME ).toString().equals( HOSTNAME ) ||
              ( registryHost != null && !registryHost.equals( HOSTNAME ) && !registryHost.equals( LOCALHOST ) ) ) {
            addFactory( instance, properties );
         }
      }
   }
   
   
   @Override public void addFactory( String name, Properties properties ) throws SchedulerException {
      // add some defaults; TODO don't overwrite existing values
      properties.put( "org.quartz.plugin.shutdownhook.class", "org.quartz.plugins.management.ShutdownHookPlugin" );
      properties.put( "org.quartz.plugin.shutdownhook.cleanShutdown", "true" );
      properties.put( "org.quartz.scheduler.skipUpdateCheck", "true" );
      
      StdSchedulerFactory factory = new StdSchedulerFactory( properties );
      
      instance2factory.put( name, factory );
   }
}

