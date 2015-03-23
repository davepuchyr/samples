// $Id: UstBondAuctionService.java 979 2013-03-06 21:08:03Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.buyside.common.bean.UstBondAnnouncement;
import com.buyside.common.bean.UstBondAuction;
import com.buyside.util.DateSerializer;

/**
 * @author dave
 *
 */
public interface UstBondAuctionService {
   /**
    * Returns the {@link DateSerializer} that the service implementation uses, which provides the {@link DateFormat} via
    * {@link DateSerializer#getDateFormat()}.
    * 
    * @return
    */
   public DateSerializer getDateSerializer();


   /**
    * Fetches and parses announcements and converts them into {@link UstBondAnnouncement} objects.
    * 
    * @return map of cusips to {@link UstBondAnnouncement}s
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws ParseException
    * @throws IOException 
    * @throws MalformedURLException 
    */
   public Map<String, UstBondAnnouncement> doAnnouncements() throws ParserConfigurationException, SAXException, ParseException, MalformedURLException, IOException;


   /**
    * Fetches and parses auction results and converts them into {@link UstBondAuction} objects.
    * 
    * @return map of cusips to {@link UstBondAuction}s
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws ParseException
    * @throws IOException 
    * @throws MalformedURLException 
    */
   public Map<String, UstBondAuction> doAuctions() throws ParserConfigurationException, SAXException, ParseException, MalformedURLException, IOException;   
   
   
   /**
    * Fields in Treasury Direct XML files. NOTE: these names are specialized for TreasuryDirectImpl as of 2013.03.17, do NOT change
    * them unless you know what you're doing.
    */
   public enum Fields { 
      AuctionAnnouncement,
      SecurityTermWeekYear,
      SecurityTermDayMonth,
      SecurityType,
      CUSIP,
      AnnouncementDate,
      AuctionDate,
      IssueDate,
      MaturityDate,
      OfferingAmount,
      CompetitiveTenderAccepted,
      NonCompetitiveTenderAccepted,
      TreasuryDirectTenderAccepted,
      TypeOfAuction,
      CompetitiveClosingTime,
      NonCompetitiveClosingTime,
      NetLongPositionReport,
      MaxAward,
      MaxSingleBid,
      CompetitiveBidDecimals,
      CompetitiveBidIncrement,
      AllocationPercentageDecimals,
      MinBidAmount,
      MultiplesToBid,
      MinToIssue,
      MultiplesToIssue,
      MatureSecurityAmount,
      CurrentlyOutstanding,
      SOMAIncluded,
      SOMAHoldings,
      FIMAIncluded,
      Series,
      InterestRate,
      FirstInterestPaymentDate,
      StandardInterestPayment,
      FrequencyInterestPayment,
      StrippableIndicator,
      MinStripAmount,
      CorpusCUSIP,
      TINTCUSIP1,
      TINTCUSIP2,
      ReOpeningIndicator,
      OriginalIssueDate,
      BackDated,
      BackDatedDate,
      LongShortNormalCoupon,
      LongShortCouponFirstIntPmt,
      InflationIndexSecurity,
      RefCPIDatedDate,
      IndexRatioOnIssueDate,
      CPIBasePeriod,
      TIINConversionFactor,
      AccruedInterest,
      DatedDate,
      AnnouncedCUSIP,
      UnadjustedPrice,
      UnadjustedAccruedInterest,
      AnnouncementPDFName,
      OriginalDatedDate,
      AdjustedAmountCurrentlyOutstanding,
      NLPExclusionAmount,
      MaximumNonCompAward,
      AdjustedAccruedInterest,
      Callable,
      CallDate,
      AuctionResults,
      PrimaryDealerTendered,
      PrimaryDealerAccepted,
      DirectBidderTendered,
      DirectBidderAccepted,
      IndirectBidderTendered,
      IndirectBidderAccepted,
      CompetitiveTendered,
      CompetitiveAccepted,
      NonCompetitiveAccepted,
      SOMATendered,
      SOMAAccepted,
      FIMATendered,
      FIMAAccepted,
      TotalTendered,
      TotalAccepted,
      BidToCoverRatio,
      ReleaseTime,
      AmountAcceptedBelowLowRate,
      HighAllocationPercentage,
      LowDiscountRate,
      HighDiscountRate,
      MedianDiscountRate,
      LowYield,
      HighYield,
      MedianYield,
      LowPrice,
      HighPrice,
      MedianPrice,
      OriginalCUSIP,
      TreasuryDirectAccepted,
      InvestmentRate,
      AdjustedPrice,
      IndexRatio,
      ResultsPDFName
   };
}
