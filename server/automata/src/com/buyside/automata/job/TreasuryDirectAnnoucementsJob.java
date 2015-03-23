// $Id: TreasuryDirectAnnoucementsJob.java 1037 2013-05-03 18:00:04Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.buyside.automata.UstBondAuctionService;
import com.buyside.common.BuySideBroadcaster;
import com.buyside.common.BuySideBroadcasterVisitor;
import com.buyside.common.BuySideTemplateEngine;
import com.buyside.common.bean.UstBondAnnouncement;
import com.buyside.common.bean.UstBondCodec;
import com.buyside.comms.MailService;
import com.buyside.comms.SmsService;
import com.buyside.db.core.UstBond;

/**
 * {@link Job} that drives the population of {@link UstBond}s with data from <a href="http://www.treasurydirect.gov/">Treasury
 * Direct</a> after announcements. Data is encoded into JSON instead of using RMI so that the job can be run on any machine.
 *
 * This class implements {@link BuySideBroadcasterVisitor} which was a failed attempt to use C++ coolness enforce type saftey.
 *
 * @author dave
 *
 */
public class TreasuryDirectAnnoucementsJob extends TreasuryDirectJob implements BuySideBroadcasterVisitor {
   final static private Logger logger = LoggerFactory.getLogger( TreasuryDirectAnnoucementsJob.class );

   /**
    * UstBondAnnouncement JSON encoder/decoder.
    */
   protected UstBondCodec<UstBondAnnouncement> codec;


   /**
    *
    * @param delimitedRecipients
    *           comma or space delimited e-mail recipients of feedback of this {@link Job}
    */
   @Inject public TreasuryDirectAnnoucementsJob( UstBondAuctionService ustbas, BuySideTemplateEngine templateEngine, MailService mailService, @Named( "com.buyside.automata.job.TreasuryDirectAnnoucementsJob.recipients" ) String delimitedRecipients, @Named( "com.buyside.automata.job.TreasuryDirectAnnoucementsJob.retries" ) int retries, @Named( "com.buyside.automata.job.TreasuryDirectAnnoucementsJob.retryDelay" ) int retryDelay ) {
      super( ustbas, templateEngine, mailService, delimitedRecipients, retries, retryDelay );

      codec = new UstBondCodec<UstBondAnnouncement>( UstBondAnnouncement.class, ustbas.getDateSerializer().getDateFormat() );
   }


   /**
    * Invokes {@link UstBondAuctionService#doAnnouncements()} and calls context.setResult( json ).
    *
    * @param context
    */
   @Override public void execute( JobExecutionContext context ) throws JobExecutionException {
      logger.trace( "Entered execute()" );

      // get existing cusips...
      JobDetail detail = context.getJobDetail();
      JobDataMap jdm = detail.getJobDataMap();
      String cusips = jdm.containsKey( KEY_CUSIPS ) ? jdm.getString( KEY_CUSIPS ) : null;

      try {
         // ...get the latest cusips...
         Map<String, UstBondAnnouncement> cusip2announcement = ustbas.doAnnouncements();
         final Set<String> latest = cusip2announcement.keySet();

         if ( latest.isEmpty() ) throw new Exception( "Parse failed on UstBondAuctionService.doAnnouncements()." );

         logger.debug( "latest == {}", latest );

         ArrayList<String> stale = new ArrayList<String>();

         // ...find existing in a thread-safe way...
         for ( String cusip : latest ) {
            if ( cusips != null && cusips.indexOf( cusip ) != -1 ) {
               stale.add( cusip );
            }
         }

         // ...delete them...
         for ( String cusip : stale ) {
            cusip2announcement.remove( cusip );
         }

         if ( cusip2announcement.size() == 0 ) logger.info( "No new cusips; TODO re-opened" ); // TODO: re-opened 
         
         // ...encode...
         String json = encode( cusip2announcement );

         // ...set
         context.setResult( json );
         jdm.put( KEY_RETRIES, 0 );
      } catch ( Exception e ) {
         retry( context, e );
      }

      logger.trace( "Exiting execute()" );
   }


   /**
    * Encodes the Map<String, {@link UstBondAnnouncement}> returned from {@link UstBondAuctionService#doAnnouncements()} into JSON
    * so that the results from local {@link Job}s and remote {@link Job} can be handled identically.
    *
    * @param cusip2announcement
    * @return JSON equivalent of cusip2announcement
    */
   protected String encode( Map<String, UstBondAnnouncement> cusip2announcement ) {
      GsonBuilder gsonb = new GsonBuilder();
      gsonb.registerTypeAdapter( UstBondAnnouncement.class, codec );

      return gsonb.create().toJson( cusip2announcement );
   }


   /**
    * Decodes the JSON generated in {@link #encode(Map)} and reconstitutes the Map<String, {@link UstBondAnnouncement}> returned
    * from {@link UstBondAuctionService#doAnnouncements()}.
    *
    * @param o
    * @return
    */
   @SuppressWarnings( "unchecked" ) public Map<String, UstBondAnnouncement> decode( Object o ) {
      // TODO: figure out why this is so clumsy and requires the @SuppressWarnings
      Map<String, UstBondAnnouncement> cusip2announcement = new HashMap<String, UstBondAnnouncement>();
      GsonBuilder gsonb = new GsonBuilder();
      gsonb.registerTypeAdapter( UstBondAnnouncement.class, codec );
      Gson gson = gsonb.create();
      cusip2announcement = (Map<String, UstBondAnnouncement>) gson.fromJson( o.toString(), cusip2announcement.getClass() );

      for ( String cusip : cusip2announcement.keySet() ) {
         Object json = cusip2announcement.get( cusip );
         cusip2announcement.put( cusip, gson.fromJson( json.toString(), UstBondAnnouncement.class ) );
      }

      return cusip2announcement;
   }


   @Override protected String getSubject( UstBond[] sorted ) {
      int i = 0, n = sorted.length;
      String[] dbnames = new String[n];

      for ( ; i < n; ++i ) dbnames[i] = sorted[i].getDbName();

      return StringUtils.join( dbnames, ", " );
   }


   /**
    * The following methods were an attempt to use a visitor pattern for type safe onSuccess/onFailure handling. It proved too
    * complicated in java since a class cannot implement the same generic interface with different types, eg
    * <PRE>
    * {@code
    * public class TreasuryDirectAnnoucementsJob implements Job, BuySideBroadcasterVisitor<MailService>, BuySideBroadcasterVisitor<SmsService>
    * }
    * </PRE>
    * @param broadcasters
    * @param bonds
    */
   public void onSuccess( List<BuySideBroadcaster> broadcasters, List<UstBond> bonds ) {
      // TODO: delete
      for ( BuySideBroadcaster broadcaster : broadcasters ) {
         broadcaster.accept( this, bonds );
      }
   }


   @SuppressWarnings( "unchecked" ) @Override public void visit( BuySideBroadcaster acceptor, Object o ) {
      // TODO: delete
      if ( o instanceof List<?> ) {
         onSuccess( acceptor, (List<UstBond>) o );
      } else {
         // onFailure( acceptor, o );
      }
   }


   public void onSuccess( BuySideBroadcaster acceptor, List<UstBond> bonds ) {
      // TODO: delete
      if ( acceptor instanceof MailService ) {
         onSuccess( ( MailService) acceptor, bonds );
      } else if ( acceptor instanceof SmsService ) {
         onSuccess( ( SmsService) acceptor, bonds );
      } else {
         // throw
      }
   }


   protected void onSuccess( SmsService smsService, List<UstBond> bonds ) {
      // TODO: delete
   }


   protected void onSuccess( MailService mailService, List<UstBond> bonds ) {
      // TODO: delete
   }


   public void onFailure( List<BuySideBroadcaster> broadcasters, Object o ) {
      // TODO: delete
   }
}

