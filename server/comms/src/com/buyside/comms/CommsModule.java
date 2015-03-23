// $Id: CommsModule.java 983 2013-03-13 22:00:20Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.comms;

import com.google.inject.Singleton;
import com.buyside.common.BuySideModule;

/**
 * @author dave
 * 
 */
@Singleton
public class CommsModule extends BuySideModule {
   public CommsModule() {
   }


   @Override public void configureModule() {
      binder().bind( MailService.class ).to( MailServiceImpl.class );
   }
}

