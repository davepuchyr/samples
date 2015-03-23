// $Id: TreasuryDirectTest.java 834 2012-10-02 21:10:41Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.spi.JobFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.buyside.common.BuySideModule;
import com.buyside.comms.CommsModule;
import com.buyside.comms.MailService;

/**
 * @author dave
 * 
 */
public class AutomataModuleTest {
   @BeforeClass static public void beforeClass() {
      String etc = System.getProperty( "user.dir" );
      etc = etc.indexOf( "automata" ) == -1 ? etc += "/etc" : etc.replace( "automata", "etc" ); // cope with build in both automata and Applications directory
      System.setProperty( BuySideModule.KEY_ETC, etc );
   }
   
   
   @Test public void test() {
      AutomataModule automataModule = new AutomataModule();
      CommsModule commsModule = new CommsModule(); // we need the CommsModule for notifications emitted by jobs
      
      Injector injector = Guice.createInjector( automataModule, commsModule );
      
      assertTrue( injector.getInstance( UstBondAuctionService.class ) instanceof TreasuryDirectImpl );
      assertTrue( injector.getInstance( SchedulerFactoryService.class ) instanceof SchedulerFactoryServiceImpl );
      assertTrue( injector.getInstance( JobFactory.class ) instanceof GuiceJobFactory );

      assertTrue( injector.getInstance( MailService.class ) != null );
   }
}
