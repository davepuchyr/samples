// $Id: MailServiceTest.java 1294 2014-02-20 19:10:35Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.comms;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeBodyPart;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.buyside.common.BuySideModule;
import com.buyside.common.BuySideTemplateEngine;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author dave
 * 
 */
public class MailServiceTest {
   private static MailService service = null;
   private static Configuration cfg = null;
   private static String user = "dave"; // sender and recipient for tests; must be a valid user
   private static Injector injector = null;

   
   /**
    * Static member initializer.
    */
   @BeforeClass static public void beforeClass() {
      String etc =  System.getProperty( "user.dir" );
      etc = etc.indexOf( "comms" ) == -1 ? etc += "/etc" : etc.replace( "comms", "etc" ); // cope with build in both comms and Applications directory

      System.setProperty( BuySideModule.KEY_ETC, etc );

      CommsModule module = new CommsModule();
      user = module.getProperties().getProperty( "com.buyside.comms.mailer.user" );
      
      injector = Guice.createInjector( module );
      service = injector.getInstance( MailService.class );
      service.setDebug( true );
      
      // Freemarker
      cfg = new Configuration();
      
      try {
         String root = new File( MailServiceTest.class.getClassLoader().getResource( "." ).getFile() ).getCanonicalPath();
         cfg.setDirectoryForTemplateLoading( new File( root + "/../templates" ) ); // HARD-CODED
      } catch ( IOException e ) {
         System.err.println( e.getMessage() );
         fail();
      }
   }
   
   
   /**
    * Test method for {@link com.buyside.comms.MailService#send( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, String type )}.
    * @throws MessagingException 
    * @throws AddressException 
    */
   @Test public void testSendPlainText() throws AddressException, MessagingException {
      service.sendText( user, new String[] { user }, null, null, this.getClass().toString() + " plain text", "plain text" );
   }


   /**
    * Test method for {@link com.buyside.comms.MailService#send( String sender, String[] recipients, String[] ccs, String[] bccs, String subject, Object content, String type )}.
    * @throws MessagingException 
    * @throws AddressException 
    * @throws IOException 
    */
   @Test public void testSendHTMLWithAttachments() throws AddressException, MessagingException, IOException {
      final Map<String, String> file2type = new HashMap<String, String>();
      file2type.put( "bloomberg_trades.20130225.csv", "text/csv" );
      file2type.put( "shapeimage_2.png", "image/png" );
      file2type.put( "MailServiceTest.xlsm", "application/vnd.ms-excel" );

      int n = file2type.keySet().size();      
      String html = "<h1>Should have " + n + " attachments</h1>\n"; 
      MimeBodyPart attachments[] = new MimeBodyPart[n];
      String pwd = MailServiceTest.class.getPackage().getName().replace( '.', File.separatorChar ) + File.separatorChar;

      for ( final String file : file2type.keySet() ) {
         MimeBodyPart attachment = new MimeBodyPart();
         DataSource ds = new FileDataSource( new File( this.getClass().getClassLoader().getResource( pwd + file ).getFile() ).getCanonicalPath() );
         attachment.setDataHandler( new DataHandler( ds ) );
         attachment.setFileName( ds.getName() );
         attachments[--n] = attachment; // note decrement
         html += "<br> &middot; " + file; 
      }
      
      service.sendHTML( user, new String[] { user }, null, null, this.getClass().toString() + " html with attachments", html, attachments );
   }


   /**
    * Test method for {@link com.buyside.comms.MailService#send( Mail mail ) and {@link BuySideTemplateEngine}..
    * @throws MessagingException 
    * @throws AddressException 
    * @throws IOException 
    * @throws TemplateException 
    */
   @Test public void testSendMail() throws AddressException, MessagingException, IOException, TemplateException {
      // attachments
      String pwd = MailServiceTest.class.getPackage().getName().replace( '.', File.separatorChar ) + File.separatorChar;
      File[] attachments = new File[] {
         new File( this.getClass().getClassLoader().getResource( pwd + "shapeimage_2.png" ).getFile() ),
         new File( this.getClass().getClassLoader().getResource( pwd + "shapeimage_2.png" ).getFile() )
      };
      
      // body template
      String hi = "World";
      String bye = "JUnit";
      Map<String, Object> input = new HashMap<String, Object>();
      input.put( "hi", hi );
      input.put( "bye", bye );

      BuySideTemplateEngine engine = injector.getInstance( BuySideTemplateEngine.class );
      TemplateLoader templateLoader = cfg.getTemplateLoader();
      String html = engine.toString( templateLoader, "HelloWorld.html", input );
      
      // mail
      Mail mail = new Mail();
      mail.setSender( user );
      mail.setRecipients( new String[] { user } );
      mail.setCcs( new String[] { user } );
      mail.setBccs( new String[] { user } );
      mail.setSubject( this.getClass().toString() + " html template body and " + attachments.length + " attachments" );
      mail.setContent( html );
      mail.setContentTypeHTML();
      mail.setAttachments( attachments );
      
      service.send( mail );
   }


   /**
    * Test method for Freemarker.
    * 
    * @throws ParseException
    */
   @Test public void testFreemarker() {
      try {
         Template template = cfg.getTemplate( "HelloWorld.html" );

         String hi = "World";
         String bye = "JUnit";
         Map<String, Object> input = new HashMap<String, Object>();
         input.put( "hi", hi );
         input.put( "bye", bye );

         Writer output = new StringWriter();
         template.process( input, output );
         output.flush();
         
         String html = output.toString();
         
         assertTrue( html.indexOf( "${hi}" ) == -1 );
         assertTrue( html.indexOf( "${bye}" ) == -1 );
         assertTrue( html.indexOf( hi ) > -1 );
         assertTrue( html.indexOf( bye ) > -1 );
      } catch (Exception e) {
        System.err.println( e.getMessage() );
        fail();
      }
   }
}
