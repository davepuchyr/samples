// $Id: TreasuryDirectJob.java 1037 2013-05-03 18:00:04Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata.job;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.buyside.automata.UstBondAuctionService;
import com.buyside.common.BuySideTemplateEngine;
import com.buyside.comms.Mail;
import com.buyside.comms.MailService;
import com.buyside.db.core.UstBond;
import freemarker.template.TemplateException;

/**
 * Base class for Treasury Direct {@link TreasuryDirectAnnouncementsJob} and {@link TreasuryDirectAuctionsJob} that provides
 * injected services that are potentially used by the derived classes, eg in a customzied {@link #onSuccess(String, List)}.
 * 
 * @author dave
 * 
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
abstract public class TreasuryDirectJob implements Job { // TODO: create abstract class BuySideJob with concrete retry() method
   final static private Logger logger = LoggerFactory.getLogger( TreasuryDirectJob.class );

   /**
    * {@link JobDataMap} key that points to cusip(s) of existing bonds. For {@link TreasuryDirectAnnouncementsJob}, cusips are the
    * WIs to double-old bonds and are used as a filter in {@link #execute(JobExecutionContext)}. For
    * {@link TreasuryDirectAuctionsJob}, the cusip(s) are bonds that have a null or negative {@link UstBond#getAuctionYield()}.
    */
   public static String KEY_CUSIPS = "cusips";
   
   /**
    * Current number of retries.
    */
   public static String KEY_RETRIES = "retries";


   protected UstBondAuctionService ustbas;
   protected BuySideTemplateEngine templateEngine;
   protected MailService mailService;
   
   /**
    * Recipients that should recieve e-mail notifications for this {@link Job}.
    */
   protected String[] recipients;
   
   /**
    * Maximum number of {@link Job#execute(JobExecutionContext)} retries.
    */
   protected int retries;

   /**
    * Milliseconds to sleep before retrying a failed job execution.
    */
   protected int retryDelay;

   /**
    * Called in {@link #onSuccess(String, List)} to set the subject line of the mail.
    * 
    * @param sorted
    */
   abstract protected String getSubject( UstBond[] sorted );
   

   /**
    * 
    * @param delimitedRecipients
    *           comma or space delimited e-mail recipients of feedback of this {@link Job}
    */
   public TreasuryDirectJob( UstBondAuctionService ustbas, BuySideTemplateEngine templateEngine, MailService mailService, String delimitedRecipients, int retries, int retryDelay ) {
      this.ustbas = ustbas;
      this.templateEngine = templateEngine;
      this.mailService = mailService;
      this.retries = retries;
      this.retryDelay = retryDelay;
      
      // remove duplicate addresses
      HashMap<String, String> map = new HashMap<String, String>();
      
      for ( String recipient : delimitedRecipients.split( "[, ]+" ) ) {
         String trimmed = StringUtils.trim( recipient );
         
         if ( trimmed != null && trimmed.length() > 0 && !map.containsKey( trimmed ) ) map.put( trimmed, trimmed );
      }
      
      Set<String> keys = map.keySet();
      recipients = keys.toArray( new String[keys.size()] );
   }
   
   
   /**
    * Retries {@link Job#execute(JobExecutionContext)} if {@link #KEY_RETRIES} is not exceeded.
    * 
    * @throws JobExecutionException
    * 
    */
   public void retry( JobExecutionContext context, Exception e ) throws JobExecutionException {
      logger.trace( "Entered retry()" );

      JobDetail detail = context.getJobDetail();
      JobDataMap jdm = detail.getJobDataMap();
      int retried = jdm.containsKey( KEY_RETRIES ) ? jdm.getInt( KEY_RETRIES ) : 0;
      JobExecutionException jee = new JobExecutionException( e, retried < retries );
      
      if ( jee.refireImmediately() ) {
         jdm.put( KEY_RETRIES, ++retried );
         logger.info( "About to sleep for {}ms before retry {}", retryDelay, retried );

         try {
            Thread.sleep( retryDelay );
         } catch ( InterruptedException e1 ) {
            logger.error( e1.getMessage(), e1 );
         }
      }
      
      logger.info( "Retry {} of {}", retried, retries );
      logger.trace( "Exiting retry()" );

      throw jee;
   }
   
   
   /**
    * Notifies com.buyside.automata.job.TreasuryDirectAnnoucementsJob.recipients of new bonds.
    * 
    * @param bonds
    * @throws TemplateException
    * @throws IOException
    * @throws MessagingException
    */
   public void onSuccess( String urlUstBondPlace, List<UstBond> bonds ) throws TemplateException, IOException, MessagingException {
      int n = bonds.size();
      UstBond[] sorted = bonds.toArray( new UstBond[n] );
      Arrays.sort( sorted, UstBond.UstBondDbNameComparator );

      Map<String, Object> input = new HashMap<String, Object>();
      input.put( "bonds", sorted );
      input.put( "urlUstBondPlace", urlUstBondPlace );
      
      String html = templateEngine.toString( "core/entity/UstBonds.html", input );
      File csv = templateEngine.toFile( "core/entity/UstBonds.csv", input );
      
      Mail mail = new Mail();
      mail.setSender( System.getProperty("user.name") + "@" + InetAddress.getLocalHost().getHostName() );
      mail.setRecipients( recipients );
      mail.setSubject( getSubject( sorted ) );
      mail.setContent( html );
      mail.setContentTypeHTML();
      mail.setAttachments( new File[] { csv } );
      
      mailService.send( mail );
      
      csv.delete();
   }
}

