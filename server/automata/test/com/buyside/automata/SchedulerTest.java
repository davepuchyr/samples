// $Id: SchedulerTest.java 1246 2013-11-25 19:35:40Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.KeyMatcher;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.buyside.automata.job.TreasuryDirectAnnoucementsJob;
import com.buyside.automata.job.TreasuryDirectAuctionsJob;
import com.buyside.common.BuySideModule;
import com.buyside.common.bean.UstBondAnnouncement;
import com.buyside.common.bean.UstBondAuction;
import com.buyside.comms.CommsModule;

/**
 * @author dave
 * 
 */
@RunWith( SchedulerRunner.class ) public class SchedulerTest {
   final static protected String testScheduler = "junit";
   static protected Injector injector = null;
   static protected SchedulerFactoryService factory = null;
   static protected String testTreasuryDirectAnnouncementsJobResult = null;
   static protected String testTreasuryDirectAuctionsJobResult = null;


   /**
    * Static member/guice initializer.
    */
   @BeforeClass static public void beforeClass() {
      String ud = System.getProperty( "user.dir" );
      String etc = ud.indexOf( "automata" ) == -1 ? ud + "/etc" : ud.replace( "automata", "etc" ); // cope with build in both automata and Applications directory

      System.setProperty( BuySideModule.KEY_ETC, etc );

      if ( ud.indexOf( "/automata" ) == -1 ) ud += "/automata"; // cope with build in both automata and Applications directory
      final String urlAnnouncements = "file://" + ud + "/test/com/buyside/automata/TreasuryOfferingAnnouncements.rss";
      final String urlAuctions = "file://" + ud + "/test/com/buyside/automata/TreasuryAuctionResults.rss";
      final String urlSchedule = "file://" + ud + "/test/com/buyside/automata/auctions.pdf";

      final AutomataModule automataModule = new AutomataModule();
      final CommsModule commsModule = new CommsModule();

      injector = Guice.createInjector( Modules.override( automataModule, commsModule ).with( new AbstractModule() {
         @Override protected void configure() {
            Properties constants = new Properties();

            // use test urls
            constants.put( "com.buyside.automata.treasurydirect.url.announcements", urlAnnouncements );
            constants.put( "com.buyside.automata.treasurydirect.url.auctions", urlAuctions );
            constants.put( "com.buyside.automata.treasurydirect.url.schedule", urlSchedule );

            Names.bindProperties( binder(), constants );
         }
      } ) );

      factory = injector.getInstance( SchedulerFactoryService.class );
   }


   /**
    * Clean-up.
    */
   @AfterClass static public void afterClass() {
      try {
         Scheduler scheduler = factory.getScheduler( testScheduler );

         scheduler.shutdown();
      } catch ( SchedulerException e ) {
         e.printStackTrace();
      }
   }


   /**
    * Adds a minimal scheduler for use within {@SchedulerTest}.
    * 
    * @throws SchedulerException
    */
   protected void addFactory() throws SchedulerException {
      Properties properties = new Properties();

      properties.put( "org.quartz.scheduler.instanceName", testScheduler );
      properties.put( "org.quartz.threadPool.threadCount", "1" );

      factory.addFactory( testScheduler, properties );
   }


   /**
    * Test method for
    * {@link com.buyside.automata.SchedulerFactoryServiceImpl#addFactory(String,org.quartz.impl.SchedulerFactory)}.
    * 
    * @throws SchedulerException
    * @throws IOException
    */
   @Test public void testAddFactory() throws SchedulerException, IOException {
      addFactory();

      Scheduler scheduler = factory.getScheduler( testScheduler );

      assertTrue( scheduler != null );
      assertTrue( scheduler.isStarted() );

      scheduler.shutdown();
   }


   /**
    * Test method for {@link com.buyside.automata.job.TreasuryDirectAnnoucementsJob}.
    * 
    * @throws SchedulerException
    * @throws IOException
    */
   @Test public void testTreasuryDirectAnnouncementsJob() throws SchedulerException {
      addFactory();

      final int nap = 5000; // max alloted time for the job
      final Object lock = new Object();
      final Thread main = Thread.currentThread();
      Scheduler scheduler = factory.getScheduler( testScheduler );
      Trigger trigger = TriggerBuilder.newTrigger().startNow().build();
      JobDetail jd = JobBuilder.newJob( TreasuryDirectAnnoucementsJob.class ).build();


      scheduler.getListenerManager().addJobListener( new JobListener() {
         @Override public String getName() {
            return "testTreasuryDirectAnnouncementsJob";
         }


         @Override public void jobToBeExecuted( JobExecutionContext context ) {
            // no-op
         }


         @Override public void jobExecutionVetoed( JobExecutionContext context ) {
            try {
               Thread.sleep( 2 * nap ); // shouldn't get here so force failure via timeout on main
            } catch ( InterruptedException e ) {
               e.printStackTrace();
            }
         }


         @Override public void jobWasExecuted( JobExecutionContext context, JobExecutionException jobException ) {
            synchronized ( lock ) {
               testTreasuryDirectAnnouncementsJobResult = context.getResult().toString(); // json
            }
            main.interrupt();
         }
      }, KeyMatcher.keyEquals( jd.getKey() ) );

      scheduler.scheduleJob( jd, trigger );

      try {
         Thread.sleep( nap ); // allow the scheduler time to execute (on its own thread)
      } catch ( InterruptedException e ) {
         synchronized ( lock ) {
            assertTrue( testTreasuryDirectAnnouncementsJobResult != null );
            assertTrue( testTreasuryDirectAnnouncementsJobResult.indexOf( "912828A42" ) != -1 );
         }

         return; // NOTE: short-circuit on success via interrupt sender JobListener
      }

      fail(); // timed out on scheduler thread
   }


   /**
    * Test method for {@link com.buyside.automata.job.TreasuryDirectAnnoucementsJob#convertResult(Object)}.
    * 
    * @throws ParseException
    */
   @Test public void testTreasuryDirectAnnouncementsJobDecode() throws ParseException {
      assertTrue( "Order of TreasuryDirectAnnouncementsJob is important; check SchedulerRunner", testTreasuryDirectAnnouncementsJobResult != null );

      UstBondAuctionService ubas = injector.getInstance( UstBondAuctionService.class );
      DateFormat formatter = ubas.getDateSerializer().getDateFormat();
      TreasuryDirectAnnoucementsJob job = injector.getInstance( TreasuryDirectAnnoucementsJob.class );
      Map<String, UstBondAnnouncement> announcements = job.decode( testTreasuryDirectAnnouncementsJobResult );

      assertTrue( announcements.containsKey( "912828A26" ) );

      UstBondAnnouncement announcement = announcements.get( "912828A26" );

      assertTrue( announcement.getCusip().equals( "912828A26" ) );
      assertTrue( announcement.getAnnouncementDate().equals( formatter.parse( "2013-11-21" ) ) );
      assertTrue( announcement.getAuctionDate().equals( formatter.parse( "2013-11-25" ) ) );
      assertTrue( announcement.getIssueDate().equals( formatter.parse( "2013-12-02" ) ) );
      assertTrue( announcement.getMaturityDate().equals( formatter.parse( "2015-11-30" ) ) );
      assertTrue( Math.abs( announcement.getOfferingAmount() / 32.0 - 1. ) < 1e-6 );

      for ( String cusip : announcements.keySet() ) {
         announcement = announcements.get( cusip );

         assertTrue( announcement.getCusip() instanceof String );
         assertTrue( announcement.getAnnouncementDate() instanceof Date );
         assertTrue( announcement.getAuctionDate() instanceof Date );
         assertTrue( announcement.getIssueDate() instanceof Date );
         assertTrue( announcement.getMaturityDate() instanceof Date );
         assertTrue( announcement.getOfferingAmount() instanceof Double );
      }
   }


   /**
    * Test method for {@link com.buyside.automata.job.TreasuryDirectAuctionsJob}.
    * 
    * @throws SchedulerException
    * @throws IOException
    */
   @Test public void testTreasuryDirectAuctionsJob() throws SchedulerException {
      addFactory();

      final int nap = 5000; // max alloted time for the job
      final Object lock = new Object();
      final Thread main = Thread.currentThread();
      Scheduler scheduler = factory.getScheduler( testScheduler );
      Trigger trigger = TriggerBuilder.newTrigger().startNow().build();
      JobDetail jd = JobBuilder.newJob( TreasuryDirectAuctionsJob.class ).build();
      
      jd.getJobDataMap().put( TreasuryDirectAuctionsJob.KEY_CUSIPS, "912828A26" );
      jd.getJobDataMap().put( TreasuryDirectAuctionsJob.KEY_AUCTIONED_TODAY, "912828A26" );

      scheduler.getListenerManager().addJobListener( new JobListener() {
         @Override public String getName() {
            return "testTreasuryDirectAuctionsJob";
         }


         @Override public void jobToBeExecuted( JobExecutionContext context ) {
            // no-op
         }


         @Override public void jobExecutionVetoed( JobExecutionContext context ) {
            try {
               Thread.sleep( 2 * nap ); // shouldn't get here so force failure via timeout on main
            } catch ( InterruptedException e ) {
               e.printStackTrace();
            }
         }


         @Override public void jobWasExecuted( JobExecutionContext context, JobExecutionException jobException ) {
            synchronized ( lock ) {
               testTreasuryDirectAuctionsJobResult = context.getResult().toString(); // json
            }
            main.interrupt();
         }
      }, KeyMatcher.keyEquals( jd.getKey() ) );

      scheduler.scheduleJob( jd, trigger );

      try {
         Thread.sleep( nap ); // allow the scheduler time to execute (on its own thread)
      } catch ( InterruptedException e ) {
         synchronized ( lock ) {
            assertTrue( testTreasuryDirectAuctionsJobResult != null );
            assertTrue( testTreasuryDirectAuctionsJobResult.indexOf( "912828A26" ) != -1 );
            // TODO: re-opened 10 and/or 30
         }

         return; // NOTE: short-circuit on success via interrupt sender JobListener
      }

      fail(); // timed out on scheduler thread
   }


   /**
    * Test method for {@link com.buyside.automata.job.TreasuryDirectAnnoucementsJob#convertResult(Object)}.
    * 
    * @throws ParseException
    */
   @Test public void testTreasuryDirectAuctionsJobDecode() throws ParseException {
      assertTrue( "Order of TreasuryDirectAuctionsJob is important; check SchedulerRunner", testTreasuryDirectAuctionsJobResult != null );

      UstBondAuctionService ubas = injector.getInstance( UstBondAuctionService.class );
      DateFormat formatter = ubas.getDateSerializer().getDateFormat();
      TreasuryDirectAuctionsJob job = injector.getInstance( TreasuryDirectAuctionsJob.class );
      Map<String, UstBondAuction> auctions = job.decode( testTreasuryDirectAuctionsJobResult );
      
      
      
      assertTrue( auctions.containsKey( "912828A26" ) );

      UstBondAuction auction = auctions.get( "912828A26" );

      assertTrue( auction.getCusip().equals( "912828A26" ) );
      assertTrue( auction.getAnnouncementDate().equals( formatter.parse( "2013-11-21" ) ) );
      assertTrue( auction.getAuctionDate().equals( formatter.parse( "2013-11-25" ) ) );
      assertTrue( auction.getIssueDate().equals( formatter.parse( "2013-12-02" ) ) );
      assertTrue( auction.getMaturityDate().equals( formatter.parse( "2015-11-30" ) ) );
      assertTrue( Math.abs( auction.getOfferingAmount() / 32.0 - 1. ) < 1e-6 );

      // TODO: re-opened 10 and/or 30

      for ( String cusip : auctions.keySet() ) {
         auction = auctions.get( cusip );

         assertTrue( auction.getCusip() instanceof String );
         assertTrue( auction.getAnnouncementDate() instanceof Date );
         assertTrue( auction.getAuctionDate() instanceof Date );
         assertTrue( auction.getIssueDate() instanceof Date );
         assertTrue( auction.getMaturityDate() instanceof Date );
         assertTrue( auction.getOfferingAmount() instanceof Double );
         assertTrue( auction.getHighYield() instanceof Float );
      }
   }


   /**
    * Test method for {@link com.buyside.automata.SchedulerRunner#computeTestMethods()}.
    */
   @Test public void testSchedulerRunner() {
      Object[] statics = new Object[] { injector, factory, testTreasuryDirectAnnouncementsJobResult };

      for ( Object o : statics ) {
         assertTrue( "Order of TreasuryDirectAnnouncementsJob is important; check SchedulerRunner", o != null );
      }
   }
}
