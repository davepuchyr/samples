// $Id: TreasuryDirectTest.java 834 2012-10-02 21:10:41Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.xml.sax.SAXException;
import com.buyside.automata.UstBondAuctionService.Fields;
import com.buyside.common.bean.UstBondAnnouncement;
import com.buyside.common.bean.UstBondAuction;

/**
 * @author dave
 * 
 */
public class TreasuryDirectTest {
   static protected TreasuryDirectImpl treasuryDirect = new TreasuryDirectImpl();
   

   /**
    * Checks common announcement and auction fields.
    * 
    * @param expected
    * @param announcement
    */
   protected void checkCommonFields( Map<Fields, Object> expected, UstBondAnnouncement announcement ) {
      for ( Fields field : expected.keySet() ) {
         String key = field.toString();
         String value = expected.get( field ).toString();
         
         switch ( field ) {
            case CUSIP:
               assertEquals( key, value, announcement.getCusip() );
               break;
            case AnnouncementDate:
               assertEquals( key, value, announcement.getAnnouncementDate().toString() );
               break;
            case AuctionDate:
               assertEquals( key, value, announcement.getAuctionDate().toString() );
               break;
            case IssueDate:
               assertEquals( key, value, announcement.getIssueDate().toString() );
               break;
            case MaturityDate:
               assertEquals( key, value, announcement.getMaturityDate().toString() );
               break;
            case OfferingAmount:
               Double test = announcement.getOfferingAmount();
               Double prod = (Double) expected.get( field );

               assertTrue( Math.abs( test / prod - 1. ) < 1e-6 );
               break;
         }
      }
   }


   /**
    * Test method for {@link com.buyside.automata.TreasuryDirectImpl#parseAnnouncements(java.io.InputStream)}.
    * @throws IOException 
    * @throws SAXException 
    * @throws ParserConfigurationException 
    */
   @Test public void testParseAnnouncements() throws ParserConfigurationException, SAXException, IOException {
      Map<String, String> expected = new HashMap<String, String>();

      expected.put( "912828A42", "http://www.treasurydirect.gov/xml/A_20131121_3.xml" );
      expected.put( "912828A34", "http://www.treasurydirect.gov/xml/A_20131121_4.xml" );
      expected.put( "912828A26", "http://www.treasurydirect.gov/xml/A_20131121_2.xml" );

      InputStream is = TreasuryDirectTest.class.getResourceAsStream( "TreasuryOfferingAnnouncements.rss" );

      Map<String, String> cusip2xml = treasuryDirect.parseXml( is );

      for ( String cusip : expected.keySet() ) {
         System.err.println( "xml == " + cusip2xml.get( cusip ) ); // dmjp
         assertTrue( cusip2xml.get( cusip ).equals( expected.get( cusip ) ) );
      }
   }


   /**
    * Test method for {@link com.buyside.automata.TreasuryDirectImpl#parseAnnouncement(java.io.InputStream)}.
    * 
    * @throws ParseException
    * @throws IOException 
    * @throws SAXException 
    * @throws ParserConfigurationException 
    */
   @Test public void testParseAnnouncement() throws ParseException, ParserConfigurationException, SAXException, IOException {
      DateFormat yyyyMMdd = treasuryDirect.getDateSerializer().getDateFormat();
      Map<Fields, Object> expected = new HashMap<Fields, Object>();

      expected.put( Fields.CUSIP, "912828A26" );

      expected.put( Fields.AnnouncementDate, yyyyMMdd.parse( "2013-11-21" ) );
      expected.put( Fields.AuctionDate, yyyyMMdd.parse( "2013-11-25" ) );
      expected.put( Fields.IssueDate, yyyyMMdd.parse( "2013-12-02" ) );
      expected.put( Fields.MaturityDate, yyyyMMdd.parse( "2015-11-30" ) );

      expected.put( Fields.OfferingAmount, new Double( 32.0 ) );

      InputStream is = TreasuryDirectTest.class.getResourceAsStream( "Announcement.xml" );

      UstBondAnnouncement announcement = treasuryDirect.parseAnnouncement( is );

      assertTrue( announcement.getTenor() == 2 ); // tenor is calculated as opposed to parsed like the Fields

      checkCommonFields( expected, announcement );
   }


   /**
    * Test method for {@link TreasuryDirectImpl#parseAuctions(InputStream)}.
    * @throws IOException 
    * @throws SAXException 
    * @throws ParserConfigurationException 
    */
   @Test public void testParseAuctions() throws ParserConfigurationException, SAXException, IOException {
      Map<String, String> expected = new HashMap<String, String>();

      expected.put( "912828A26", "http://www.treasurydirect.gov/xml/R_20131125_3.xml" );

      InputStream is = TreasuryDirectTest.class.getResourceAsStream( "TreasuryAuctionResults.rss" );

      Map<String, String> cusip2xml = treasuryDirect.parseXml( is );

      for ( String cusip : expected.keySet() ) {
         assertTrue( cusip2xml.get( cusip ).equals( expected.get( cusip ) ) );
      }
   }


   /**
    * Test method for {@link TreasuryDirectImpl#parseAuction(java.io.InputStream)}.
    * 
    * @throws ParseException
    * @throws IOException 
    * @throws SAXException 
    * @throws ParserConfigurationException 
    */
   @Test public void testParseAuction() throws ParseException, IOException, ParserConfigurationException, SAXException {
      DateFormat yyyyMMdd = treasuryDirect.getDateSerializer().getDateFormat();
      Map<Fields, Object> expected = new HashMap<Fields, Object>();

      expected.put( Fields.CUSIP, "912828US7" );

      expected.put( Fields.AnnouncementDate, yyyyMMdd.parse( "2013-03-07" ) );
      expected.put( Fields.AuctionDate, yyyyMMdd.parse( "2013-03-12" ) );
      expected.put( Fields.IssueDate, yyyyMMdd.parse( "2013-03-15" ) );
      expected.put( Fields.MaturityDate, yyyyMMdd.parse( "2016-03-15" ) );
      expected.put( Fields.DatedDate, yyyyMMdd.parse( "2013-03-15" ) );

      expected.put( Fields.HighYield, new Float( 0.411 ) );

      InputStream is = TreasuryDirectTest.class.getResourceAsStream( "R_20130312_2.xml" );

      UstBondAuction auction = treasuryDirect.parseAuction( is );

      assertTrue( auction.getTenor() == 3 ); // tenor is calculated as opposed to parsed like the Fields
      
      checkCommonFields( expected, auction );
      
      Fields[] fields = new Fields[] {
         Fields.DatedDate,
         Fields.HighYield
      };
      
      for ( Fields field : fields ) {
         switch ( field ) {
            case DatedDate:
               assertTrue( ( auction.getDatedDate().equals( expected.get( field ) ) ) );
               break;
            case HighYield:
               Float test = auction.getHighYield();
               Float prod = (Float) expected.get( field );

               assertTrue( Math.abs( test / prod - 1. ) < 1e-6 );
               break;
         }
      }
   }
}
