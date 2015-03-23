/**
 * 
 */
package com.buyside.comms;

import java.io.IOException;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import com.buyside.common.BuySideBroadcaster;
import com.buyside.common.BuySideBroadcasterVisitor;

/**
 * @author dave
 *
 */
public abstract class MailService implements BuySideBroadcaster {
   protected Properties properties = new Properties();
   
   public abstract void send( Mail mail ) throws IOException, MessagingException;
   
   public abstract void send( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, String type, MimeBodyPart[] attachments ) throws AddressException, MessagingException;

   public abstract void sendHTML( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content ) throws AddressException, MessagingException;
   public abstract void sendHTML( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, MimeBodyPart[] attachments ) throws AddressException, MessagingException;
   
   public abstract void sendText( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content ) throws AddressException, MessagingException;
   public abstract void sendText( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, MimeBodyPart[] attachments ) throws AddressException, MessagingException;
   
   public abstract void setDebug( boolean debug );
   

   public void accept( BuySideBroadcasterVisitor visitor, Object o ) {
      visitor.visit( this, o );
   }
}
