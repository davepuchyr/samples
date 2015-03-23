// $Id$
package com.avaritia.app.zingers.server.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Base class for any tests that need the {@link OfyService#ofy()} and/or rely upon data being in the datastore. If a service that
 * uses the datastore is not derived from this class it'll get a
 *
 * <pre>
 * NullPointerException: No API environment is registered for this thread.
 * </pre>
 *
 * exception.
 */
public class OfyServiceTest {
   static final private LocalServiceTestHelper helper = new LocalServiceTestHelper( new LocalDatastoreServiceTestConfig() );


   @BeforeClass static public void beforeClass() {
      helper.setUp();

      Injector injector = Guice.createInjector( new AbstractModule() {
         @Override protected void configure() {
            bind( OfyService.class ).in( Singleton.class );
         }
      } );

      injector.getInstance( OfyService.class ).contextInitialized( null ); // NOTE
   }


   @AfterClass static public void tearDown() {
      helper.tearDown();
   }
}
