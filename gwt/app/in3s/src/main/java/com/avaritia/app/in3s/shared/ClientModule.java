// $Id$
package com.avaritia.app.in3s.shared;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import com.avaritia.app.in3s.client.FiniteStateMachine;
import com.avaritia.app.in3s.client.presenter.AuthenticatedView;
import com.avaritia.app.in3s.client.presenter.AuthorizedView;
import com.avaritia.app.in3s.client.presenter.D3View;
import com.avaritia.app.in3s.client.presenter.HelpView;
import com.avaritia.app.in3s.client.presenter.HomeView;
import com.avaritia.app.in3s.client.presenter.PhoneGapView;
import com.avaritia.app.in3s.client.presenter.SettingsView;
import com.avaritia.app.in3s.client.presenter.UnauthenticatedView;
import com.avaritia.app.in3s.client.presenter.UnauthorizedView;
import com.avaritia.app.in3s.rebind.AppBundleGenerator;
import com.avaritia.lib.console.client.Console;
import com.avaritia.lib.injector.shared.ConstantsBinder;
import com.avaritia.lib.java.fsm.shared.Logger;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;

/**
 * This is invoked like rebind() in {@link AppBundleGenerator} but has to be available on the client side; hence its placement
 * in the shared package, not rebind.  NOTE: GWT.create() is used to instantiate classes that are not specifically set with bind()
 * and does deferred binding, so gwt.xml mappings are in effect.
 */
public class ClientModule extends AbstractGinModule {
   @Override protected void configure() {
      // bind potential overrides
      try {
         String dir = System.getProperty( "user.dir", "NOTE: single arg getProperty() is somehow not available" );
         String file =  dir.replace( "war", "" ) + "/src/main/java/com/avaritia/app/in3s/shared/injection.properties"; // HARD-CODED
         InputStream input = new FileInputStream( file );
         ConstantsBinder constantsBinder = new ConstantsBinder();

         constantsBinder.bind( binder(), input );

         input.close();
      } catch ( FileNotFoundException e ) {
         e.printStackTrace();
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      // console
      requestStaticInjection( Console.class );

      // fsm
      bind( FiniteStateMachine.class ).in( Singleton.class );
      bind( Logger.class ).to( com.avaritia.lib.java.fsm.client.LoggerImpl.class  ).in( Singleton.class );

      requestStaticInjection( FiniteStateMachine.class );

      // misc
      bind( PhoneGap.class ).in( Singleton.class );

      // app
      bind( EventBus.class            ).to( SimpleEventBus.class                                     ).in( Singleton.class );
      bind( PlaceController.class     ).to( com.avaritia.lib.place.shared.PlaceController.class      ).in( Singleton.class );
      bind( PlaceHistoryHandler.class ).to( com.avaritia.app.in3s.client.PlaceHistoryHandler.class ).in( Singleton.class );
      bind( PlaceHistoryMapper.class  ).to( com.avaritia.app.in3s.client.PlaceHistoryMapper.class  ).in( Singleton.class );

      // main
      bind( AuthenticatedView.class   ).to( com.avaritia.app.in3s.client.ui.AuthenticatedView.class   ).in( Singleton.class );
      bind( AuthorizedView.class      ).to( com.avaritia.app.in3s.client.ui.AuthorizedView.class      ).in( Singleton.class );
      bind( D3View.class              ).to( com.avaritia.app.in3s.client.ui.D3View.class              ).in( Singleton.class );
      bind( HelpView.class            ).to( com.avaritia.app.in3s.client.ui.HelpView.class            ).in( Singleton.class );
      bind( HomeView.class            ).to( com.avaritia.app.in3s.client.ui.HomeView.class            ).in( Singleton.class );
      bind( PhoneGapView.class        ).to( com.avaritia.app.in3s.client.ui.PhoneGapView.class        ).in( Singleton.class );
      bind( SettingsView.class        ).to( com.avaritia.app.in3s.client.ui.SettingsView.class        ).in( Singleton.class );
      bind( UnauthenticatedView.class ).to( com.avaritia.app.in3s.client.ui.UnauthenticatedView.class ).in( Singleton.class );
      bind( UnauthorizedView.class    ).to( com.avaritia.app.in3s.client.ui.UnauthorizedView.class    ).in( Singleton.class );

      //install( new GinFactoryModuleBuilder().build( AssistedInjectionFactory.class ) );
   }
}
