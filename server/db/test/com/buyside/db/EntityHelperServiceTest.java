// $Id: EntityHelperServiceTest.java 1233 2013-11-08 20:16:50Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db;

import static org.junit.Assert.assertTrue;
import java.util.Map;
import org.junit.Test;
import com.buyside.db.core.Counterparty;
import com.buyside.db.core.Firm;
import com.buyside.db.core.UstBond;
import com.buyside.db.core.Vector;
import com.buyside.db.pnl.Action;
import com.buyside.db.pnl.ExecType;
import com.buyside.db.pnl.Market;
import com.buyside.db.pnl.ReportType;
import com.buyside.db.pnl.TradeType;

/**
 * @author dave
 */
public class EntityHelperServiceTest {
   @Test public void testTable() {
      EntityHelperService ehs = new EntityHelperServiceImpl();

      assertTrue( "UST_BONDS".equals( ehs.getEntityDefinition( UstBond.class ).getTable() ) );
   }
   
   
   @Test public void testColumns() {
      EntityHelperService ehs = new EntityHelperServiceImpl();
      
      assertTrue( ehs.getEntityDefinition( Action.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( Counterparty.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( ExecType.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( Firm.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( Market.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( ReportType.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( TradeType.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( UstBond.class ).getColumns().size() > 0 );
      assertTrue( ehs.getEntityDefinition( Vector.class ).getColumns().size() > 0 );
   }

   
   @Test public void testSynonyms() {
      EntityHelperService ehs = new EntityHelperServiceImpl();
      Map<String, String> synonyms = ehs.getEntityDefinition( UstBond.class ).getColumnSynonyms();

      assertTrue( synonyms.get( "ACCIDENTALREOPENAUCTIONDATE1" ).equals( "ACCIDENTAL_REOPEN_AUCTION_DATE_1" ) );
   }
}
