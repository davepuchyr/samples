// $Id: DbModule.java 994 2013-03-25 15:27:15Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db;

import com.google.inject.Singleton;
import com.buyside.common.BuySideModule;

/**
 * @author dave
 * 
 */
@Singleton
public class DbModule extends BuySideModule {
   public DbModule() {
   }

   
   @Override public void configureModule() {
      binder().bind( EntityHelperService.class ).to( EntityHelperServiceImpl.class );
   }
}

