// $Id$
package com.avaritia.app.zingers.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.avaritia.app.zingers.server.service.CategoryService;
import com.avaritia.app.zingers.server.service.ZingerService;
import com.avaritia.lib.injector.server.ConstantsBinder;
import com.avaritia.lib.server.OfyRequestFactoryServletModule;
import com.google.inject.Singleton;

/**
 * Effectively replaces web.xml (after appropriate entries in web.xml are made).
 *
 * @see <a href=https://github.com/google/guice/wiki/ServletModule#the-binding-language>The Binding Language</a>
 */
public class ServerModule extends OfyRequestFactoryServletModule {
   @Override protected final void configureDerivedServlets() {
      bind( CategoryService.class ).in( Singleton.class );
      bind( ZingerService.class ).in( Singleton.class );

      serve( "/zinger" ).with( ZingerServlet.class );

      try {
         ConstantsBinder constantsBinder = new ConstantsBinder();
         InputStream input = com.avaritia.app.zingers.shared.ClientModule.class.getResourceAsStream( "injection.properties" ); // HARD-CODED

         constantsBinder.bind( binder(), input );

         input.close();
      } catch ( FileNotFoundException e ) {
         e.printStackTrace();
      } catch ( IOException e ) {
         e.printStackTrace();
      }
   }
}
