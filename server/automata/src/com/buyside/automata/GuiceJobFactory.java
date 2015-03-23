// $Id: GuiceJobFactory.java 983 2013-03-13 22:00:20Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Guice dependency injector for Quartz {@link Job}s.
 */
@Singleton
public final class GuiceJobFactory implements JobFactory {
   private final Injector injector;


   @Inject public GuiceJobFactory( final Injector injector ) {
      this.injector = injector;
   }


   @Override public Job newJob( TriggerFiredBundle bundle, Scheduler scheduler ) throws SchedulerException {
      JobDetail jobDetail = bundle.getJobDetail();
      Class<? extends Job> jobClass = jobDetail.getJobClass();
      
      return injector.getInstance( jobClass );
   }
}
