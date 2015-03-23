// $Id: MailServiceImpl.java 1027 2013-04-23 18:20:48Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.comms;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;




/**
 * @author dave
 */
public class MailServiceImpl extends MailService {
   final static private Logger logger = LoggerFactory.getLogger( MailServiceImpl.class );

   @Inject @Named( "com.buyside.comms.mailer.host" ) private String host;
   @Inject @Named( "com.buyside.comms.mailer.transport.protocol" ) private String protocol;


   public MailServiceImpl() {
   }


   @Override public void send( Mail mail ) throws IOException, MessagingException {
      File[] files = mail.getAttachments();
      int i = 0, n = files.length;
      MimeBodyPart[] attachments = n > 0 ? new MimeBodyPart[n] : null;
      
      for ( ; i < n; ++i ) {
         File file = files[i];
         MimeBodyPart attachment = new MimeBodyPart();
         DataSource ds = new FileDataSource( file.getCanonicalPath() );

         attachment.setDataHandler( new DataHandler( ds ) );
         attachment.setFileName( ds.getName() );
         
         attachments[i] = attachment;
      }

      send( mail.getSender(), mail.getRecipients(), mail.getCcs(), mail.getBccs(), mail.getSubject(), mail.getContent(), mail.getContentType(), attachments );
   }
   
   
   @Override public void send( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, String type, MimeBodyPart[] attachments ) throws MessagingException {
      InternetAddress iaSender = new InternetAddress( sender );
      InternetAddress[] iaRecipients = new InternetAddress[recipients.length];
      InternetAddress[] iaCCs = new InternetAddress[ccs != null ? ccs.length : 0];
      InternetAddress[] iaBCCs = new InternetAddress[bccs != null ? bccs.length : 0];
      
      // TODO: assert sender, at least 1 recipient, object
         
      for ( int i = 0; i < iaRecipients.length; ++i ) {
         iaRecipients[i] = new InternetAddress( recipients[i] );
      }
         
      for ( int i = 0; i < iaCCs.length; ++i ) {
         iaCCs[i] = new InternetAddress( ccs[i] );
      }
   
      for ( int i = 0; i < iaBCCs.length; ++i ) {
         iaBCCs[i] = new InternetAddress( bccs[i] );
      }           
      
      //http://www.rgagnon.com/javadetails/java-0504.html
      properties.setProperty( "mail.transport.protocol", protocol );
      properties.setProperty( "mail.host", host );
      
      Session mailSession = Session.getDefaultInstance( properties, null );
      MimeMessage message = new MimeMessage( mailSession );
      
      message.setSentDate( new Date() );
      message.setFrom( iaSender );
      
      for ( InternetAddress ia : iaRecipients ) message.addRecipient( Message.RecipientType.TO, ia );
      for ( InternetAddress ia : iaCCs ) message.addRecipient( Message.RecipientType.CC, ia );
      for ( InternetAddress ia : iaBCCs ) message.addRecipient( Message.RecipientType.BCC, ia );
      
      message.setSubject( subject );
      
      if ( attachments == null || attachments.length == 0 ) {
         message.setContent( content, type );
      } else {
         /* inline image
         MimeMultipart multipart = new MimeMultipart( "related" );
         BodyPart messageBodyPart = new MimeBodyPart();

         messageBodyPart.setContent( content, type );
         multipart.addBodyPart( messageBodyPart );
         
         // second part (the image)
         messageBodyPart = new MimeBodyPart();
         DataSource fds = new FileDataSource("C:\\images\\jht.gif");
         messageBodyPart.setDataHandler(new DataHandler(fds));
         messageBodyPart.setHeader("Content-ID","<image>");
         
         // add it
         multipart.addBodyPart(messageBodyPart);
   
         // put everything together
         message.setContent(multipart);
         */

         Multipart multipart = new MimeMultipart();
         MimeBodyPart messagePart = new MimeBodyPart();
         messagePart.setContent( content, type );
         
         multipart.addBodyPart( messagePart );
         for ( MimeBodyPart part : attachments ) multipart.addBodyPart( part );
         
         message.setContent( multipart );
      }
      
      message.saveChanges();

      Transport.send( message );
      
      logger.info( "Sender {} sent '" + subject + "' to {}", sender, StringUtils.join( recipients, ", " ) );
   }


   @Override public void sendHTML( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content ) throws AddressException, MessagingException {
      send( sender, recipients, ccs, bccs, subject, content, "text/html", new MimeBodyPart[0] );
   }


   @Override public void sendHTML( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, MimeBodyPart[] attachments ) throws AddressException, MessagingException {
      send( sender, recipients, ccs, bccs, subject, content, "text/html", attachments );
   }


   @Override public void sendText( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content ) throws AddressException, MessagingException {
      send( sender, recipients, ccs, bccs, subject, content, "text/plain", new MimeBodyPart[0] );
   }


   @Override public void sendText( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, MimeBodyPart[] attachments ) throws AddressException, MessagingException {
      send( sender, recipients, ccs, bccs, subject, content, "text/plain", attachments );
   }


   @Override public void setDebug( boolean debug ) {
      properties.put( "mail.debug", debug ? "true" : "false" );
   }
}
