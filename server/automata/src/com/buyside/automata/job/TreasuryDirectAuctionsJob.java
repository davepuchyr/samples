// $Id: TreasuryDirectAuctionsJob.java 1040 2013-05-08 02:32:26Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata.job;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.buyside.common.BuySideTemplateEngine;
import com.buyside.common.bean.UstBondAnnouncement;
import com.buyside.common.bean.UstBondAuction;
import com.buyside.common.bean.UstBondCodec;
import com.buyside.comms.MailService;
import com.buyside.db.core.UstBond;

/**
 * {@link Job} that drives the population of {@link UstBond}s with data from <a href="http://www.treasurydirect.gov/">Treasury
 * Direct</a> after auctions.  Data is encoded into JSON instead of using RMI so that the job can be run on any machine. 
 * 
 * @author dave
 * 
 */
public class TreasuryDirectAuctionsJob extends TreasuryDirectJob implements Job {
   final static private Logger logger = LoggerFactory.getLogger( TreasuryDirectAuctionsJob.class );

   /**
    * {@link JobDataMap} key that points to the cusip of a bond being auctioned today.
    */
   public static String KEY_AUCTIONED_TODAY = "KEY_AUCTIONED_TODAY";
   
   /**
    * UstBondAuction JSON encoder/decoder.
    */
   protected UstBondCodec<UstBondAuction> codec;

   
   /**
    * 
    * @param delimitedRecipients
    *           comma or space delimited e-mail recipients of feedback of this {@link Job}
    */
   @Inject public TreasuryDirectAuctionsJob( UstBondAuctionService ustbas, BuySideTemplateEngine templateEngine, MailService mailService, @Named( "com.buyside.automata.job.TreasuryDirectAuctionsJob.recipients" ) String delimitedRecipients, @Named( "com.buyside.automata.job.TreasuryDirectAuctionsJob.retries" ) int retries, @Named( "com.buyside.automata.job.TreasuryDirectAuctionsJob.retryDelay" ) int retryDelay ) {
      super( ustbas, templateEngine, mailService, delimitedRecipients, retries, retryDelay );

      codec = new UstBondCodec<UstBondAuction>( UstBondAuction.class, ustbas.getDateSerializer().getDateFormat() );
   }
   
   
   /**
    * Uses {@link UstBondAuctionService#doAuctions()} to fetch auction info.
    * 
    * @param context
    *           must include a {@link JobDataMap} with key {@link TreasuryDirectJob#KEY_CUSIPS} that points to the cusip of the bond
    *           of interest.
    */
   @Override public void execute( JobExecutionContext context ) throws JobExecutionException {
      logger.trace( "Entered execute()" );

      // get sought cusips...
      JobDetail detail = context.getJobDetail();
      JobDataMap jdm = detail.getJobDataMap();
      String cusips = jdm.containsKey( KEY_CUSIPS ) ? jdm.getString( KEY_CUSIPS ) : null;
      String auctionedToday = jdm.containsKey( KEY_AUCTIONED_TODAY ) ? jdm.getString( KEY_AUCTIONED_TODAY ) : null;

      logger.debug( "cusips == {}; auctionedToday == {}", cusips, auctionedToday );
      
      if ( cusips.isEmpty() ) return; // short-circuit

      try {
         // ...get the latest cusips...
         Map<String, UstBondAuction> cusip2auction = ustbas.doAuctions();
         final Set<String> latest = cusip2auction.keySet();

         if ( latest.isEmpty() ) throw new Exception( "Parse failed on UstBondAuctionService.doAuctions()." );
         
         logger.debug( "latest == {}", latest );
         
         ArrayList<String> stale = new ArrayList<String>();
   
         // ...find existing in a thread-safe way...
         for ( String cusip : latest ) {
            if ( cusips != null && cusips.indexOf( cusip ) == -1 ) {
               stale.add( cusip );
            }
         }
   
         // ...delete them...
         for ( String cusip : stale ) {
            cusip2auction.remove( cusip );
         }

         if ( cusip2auction.size() == 0 ) logger.info( "No new cusips; TODO re-opened" ); // TODO: re-opened 
         
         if ( auctionedToday != null && ( cusip2auction.size() == 0 || !cusip2auction.containsKey( auctionedToday ) ) ) {
            throw new Exception( auctionedToday + " not found on its auction day." );
         }
   
         // ...encode...
         String json = encode( cusip2auction );
   
         // ...and set
         context.setResult( json );
         jdm.put( KEY_RETRIES, 0 );
      } catch ( Exception e ) {
         retry( context, e );
      }

      logger.trace( "Exiting execute()" );
   }


   /**
    * Encodes the Map<String, {@link UstBondAuction}> returned from {@link UstBondAuctionService#doAuctions()} into JSON
    * so that the results from local {@link Job}s and remote {@link Job} can be handled identically.
    * 
    * @param cusip2auction
    * @return JSON equivalent of cusip2auction
    */
   protected String encode( Map<String, UstBondAuction> cusip2auction ) {
      GsonBuilder gsonb = new GsonBuilder();
      gsonb.registerTypeAdapter( UstBondAuction.class, codec );
      
      return gsonb.create().toJson( cusip2auction );
   }


   /**
    * Decodes the JSON generated in {@link #encode(Map)} and reconstitutes the Map<String, {@link UstBondAnnouncement}> returned
    * from {@link UstBondAuctionService#doAnnouncements()}.
    * 
    * @param o
    * @return
    */
   @SuppressWarnings( "unchecked" ) public Map<String, UstBondAuction> decode( Object o ) {
      // TODO: figure out why this is so clumsy and requires the @SuppressWarnings
      Map<String, UstBondAuction> cusip2auction = new HashMap<String, UstBondAuction>();
      GsonBuilder gsonb = new GsonBuilder();
      gsonb.registerTypeAdapter( UstBondAuction.class, codec );
      Gson gson = gsonb.create();
      cusip2auction = (Map<String, UstBondAuction>) gson.fromJson( o.toString(), cusip2auction.getClass() );
      
      for ( String cusip : cusip2auction.keySet() ) {
         Object json = cusip2auction.get( cusip );
         cusip2auction.put( cusip, gson.fromJson( json.toString(), UstBondAuction.class ) );
      }

      return cusip2auction;
   }


   @Override protected String getSubject( UstBond[] sorted ) {
      int i = 0, n = sorted.length;
      String[] dbnames = new String[n];
      
      for ( ; i < n; ++i ) dbnames[i] = String.format(  "GT%02d", sorted[i].getTenor() );         
      
      return "The new " + StringUtils.join( dbnames, ", " );
   }
}

