// $Id: TreasuryDirectImpl.java 1246 2013-11-25 19:35:40Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.buyside.common.bean.UstBondAnnouncement;
import com.buyside.common.bean.UstBondAuction;
import com.buyside.util.DateSerializer;

/**
 * Parser of <a href="http://www.treasurydirect.gov/">Treasury Direct</a> bond announcements and auctions.
 * 
 * @author dave
 */
@Singleton public class TreasuryDirectImpl implements UstBondAuctionService {
   final static private Logger logger = LoggerFactory.getLogger( TreasuryDirectImpl.class );

   /**
    * Date format used by TreasuryDirect in its XML.
    */
   protected DateSerializer dateSerializer = new DateSerializer( DateSerializer.DATE_FORMATS.CUSTOM.toString(), "yyyy-MM-dd" );

   
   @Override public DateSerializer getDateSerializer() {
      return dateSerializer;
   }
   
   
   @Inject @Named( "com.buyside.automata.treasurydirect.url.announcements" ) String urlAnnouncements;
   @Inject @Named( "com.buyside.automata.treasurydirect.url.auctions" ) String urlAuctions;
   @Inject @Named( "com.buyside.automata.treasurydirect.url.schedule" ) String urlSchedule;


   public TreasuryDirectImpl() {
   }


   public Map<String, String> parseXml( InputStream is ) throws ParserConfigurationException, SAXException, IOException {
      Map<String, String> cusip2xml = new HashMap<String, String>();

      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      docBuilderFactory.setIgnoringElementContentWhitespace( true );

      Document doc = docBuilder.parse( is );
      JXPathContext context = JXPathContext.newContext( doc );
      
      logger.debug( "parseXml: context == {}", context );

      context.getVariables().declareVariable( "notes", "Note" );
      context.getVariables().declareVariable( "bonds", "Bond" );

      @SuppressWarnings( "rawtypes" ) Iterator bonds = context.iterate( "//category[contains(@domain,$notes) or contains(@domain,$bonds)]/parent::node()/description" );

      while ( bonds.hasNext() ) {
         Object o = bonds.next();
         String html = "<html>" + StringEscapeUtils.unescapeHtml4( o.toString() ) + "</html>";
         Document subdoc = docBuilder.parse( new ByteArrayInputStream( html.getBytes() ) );
         JXPathContext cxt = JXPathContext.newContext( subdoc );

         cxt.getVariables().declareVariable( "xml", "xml" );
         cxt.getVariables().declareVariable( "cusip", "CUSIP" );

         Node href = (Node) cxt.selectSingleNode( "//a[contains(@href,$xml)]" );
         Node cusip = ( (Node) cxt.selectSingleNode( "//strong[contains(text(),$cusip)]" ) ).getNextSibling();

         logger.trace( "parseXml: cusip == {}; href == {}", cusip.getTextContent().trim(), href.getAttributes().getNamedItem( "href" ).getTextContent().trim() );

         cusip2xml.put( cusip.getTextContent().trim(), href.getAttributes().getNamedItem( "href" ).getTextContent().trim() );
      }

      logger.debug( "parseXml: cusips == {}", cusip2xml.keySet() );

      return cusip2xml;
   }

   
   protected Map<String, String> fetch( String surl ) throws IOException, MalformedURLException, ParserConfigurationException, SAXException {
      logger.info( "fetch( {} )", surl );

      Map<String, String> cusip2xml = null;
      URL url = new URL( surl );
      InputStream stream = url.openStream();
      cusip2xml = parseXml( stream );

      return cusip2xml;
   }


   public Map<String, String> fetchAnnouncements() throws IOException, MalformedURLException, ParserConfigurationException, SAXException {
      return fetch( urlAnnouncements );
   }


   public Map<String, String> fetchAuctions() throws IOException, MalformedURLException, ParserConfigurationException, SAXException {
      return fetch( urlAuctions );
   }


   @Override public Map<String, UstBondAnnouncement> doAnnouncements() throws ParserConfigurationException, SAXException, ParseException, MalformedURLException, IOException {
      Map<String, UstBondAnnouncement> cusip2announcement = new HashMap<String, UstBondAnnouncement>();
      Map<String, String> cusip2xml = fetchAnnouncements();
      Set<String> cusips = cusip2xml.keySet();

      for ( String cusip : cusips ) {
         URL url = new URL( cusip2xml.get( cusip ) );
         InputStream stream = url.openStream();
         cusip2announcement.put( cusip, parseAnnouncement( stream ) );
      }

      return cusip2announcement;
   }


   @Override public Map<String, UstBondAuction> doAuctions() throws ParserConfigurationException, SAXException, ParseException, MalformedURLException, IOException {
      Map<String, UstBondAuction> cusip2auction = new HashMap<String, UstBondAuction>();
      Map<String, String> cusip2xml = fetchAuctions();
      Set<String> cusips = cusip2xml.keySet();
      
      for ( String cusip : cusips ) {
         URL url = new URL( cusip2xml.get( cusip ) );
         InputStream stream = url.openStream();
         cusip2auction.put( cusip, parseAuction( stream ) );
      }
      
      return cusip2auction;
   }


   protected Document getDocument( InputStream is ) throws ParserConfigurationException, SAXException, IOException {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      docBuilderFactory.setIgnoringElementContentWhitespace( true );

      return docBuilder.parse( is );
   }
   
   
   protected UstBondAnnouncement parseAnnouncement( InputStream is ) throws ParserConfigurationException, SAXException, IOException, ParseException {
      Document doc = getDocument( is );
      
      return parseAnnouncementDocument( doc );
   }
   
   
   protected UstBondAnnouncement parseAnnouncementDocument( Document doc ) throws ParseException {
      UstBondAnnouncement announcement = new UstBondAnnouncement();
      JXPathContext context = JXPathContext.newContext( doc );
      DateFormat df = dateSerializer.getDateFormat();
      Fields[] fields = new Fields[] { Fields.CUSIP, Fields.AnnouncementDate, Fields.AuctionDate, Fields.DatedDate, Fields.FirstInterestPaymentDate, Fields.IssueDate, Fields.MaturityDate, Fields.OfferingAmount, Fields.OriginalDatedDate };

      for ( Fields field : fields ) {
         String name = field.toString();
         Node value = (Node) context.selectSingleNode( "//AuctionAnnouncement/" + name );
         String string = value.getTextContent().trim();

         logger.trace( "parseAnnouncementDocument: {} == {}", name, string );

         switch ( Fields.valueOf( name ) ) {
            case CUSIP:
               announcement.setCusip( string );
               break;
            case AnnouncementDate:
               announcement.setAnnouncementDate( df.parse( string ) );
               break;
            case AuctionDate:
               announcement.setAuctionDate( df.parse( string ) );
               break;
            case DatedDate:
               announcement.setDatedDate( df.parse( string ) );
               break;
            case FirstInterestPaymentDate:
               announcement.setFirstInterestPaymentDate( df.parse( string ) );
               break;
            case IssueDate:
               announcement.setIssueDate( df.parse( string ) );
               break;
            case MaturityDate:
               announcement.setMaturityDate( df.parse( string ) );
               break;
            case OfferingAmount:
               announcement.setOfferingAmount( Double.parseDouble( string ) );
               break;
            case OriginalDatedDate:
               if ( !string.isEmpty() ) {
                  announcement.setOriginalDatedDate( df.parse( string ) );
               }
               break;
         }
      }

      return announcement;
   }


   protected UstBondAuction parseAuction( InputStream is ) throws IOException, ParserConfigurationException, SAXException, ParseException {
      Document doc = getDocument( is );
      UstBondAnnouncement announcement = parseAnnouncementDocument( doc );
      UstBondAuction auction = new UstBondAuction( announcement );
      
      JXPathContext context = JXPathContext.newContext( doc );
      Fields[] fields = new Fields[] { Fields.BidToCoverRatio, Fields.CompetitiveAccepted, Fields.DirectBidderAccepted, Fields.HighYield, Fields.IndirectBidderAccepted, Fields.InterestRate, Fields.PrimaryDealerAccepted, Fields.SOMAAccepted };

      for ( Fields field : fields ) {
         String name = field.toString();
         Node value = (Node) context.selectSingleNode( "//AuctionResults/" + name );
         String string = value != null ? value.getTextContent().trim() : "";
         
         logger.trace( "parseAuction: {} == {}", name, string );

         switch ( Fields.valueOf( name ) ) {
            case BidToCoverRatio:
               auction.setBidToCoverRatio( Float.parseFloat( string ) );
               break;
            case CompetitiveAccepted:
               auction.setCompetitiveAccepted( Double.parseDouble( string ) );
               break;
            case DirectBidderAccepted:
               auction.setDirectBidderAccepted( Double.parseDouble( string ) );
               break;
            case HighYield:
               auction.setHighYield( Float.parseFloat( string ) );
               break;
            case IndirectBidderAccepted:
               auction.setIndirectBidderAccepted( Double.parseDouble( string ) );
               break;
            case InterestRate:
               auction.setInterestRate( Float.parseFloat( string ) );
               break;
            case PrimaryDealerAccepted:
               auction.setPrimaryDealerAccepted( Double.parseDouble( string ) );
               break;
            case SOMAAccepted:
               auction.setSomaAccepted( Double.parseDouble( string ) );
               break;
         }
      }

      return auction;
   }
}
