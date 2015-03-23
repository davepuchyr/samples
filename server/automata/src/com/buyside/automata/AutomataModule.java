// $Id: AutomataModule.java 983 2013-03-13 22:00:20Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.automata;

import org.quartz.spi.JobFactory;
import com.google.inject.Singleton;
import com.buyside.automata.job.TreasuryDirectAnnoucementsJob;
import com.buyside.automata.job.TreasuryDirectAuctionsJob;
import com.buyside.common.BuySideModule;

/**
 * @author dave
 * 
 */
@Singleton
public class AutomataModule extends BuySideModule {
   public AutomataModule() {
   }


   @Override protected void configureModule() {
      binder().bind( UstBondAuctionService.class ).to( TreasuryDirectImpl.class );
      binder().bind( SchedulerFactoryService.class ).to( SchedulerFactoryServiceImpl.class );
      binder().bind( JobFactory.class ).to( GuiceJobFactory.class );

      binder().bind( TreasuryDirectAnnoucementsJob.class );
      binder().bind( TreasuryDirectAuctionsJob.class );
   }
}

