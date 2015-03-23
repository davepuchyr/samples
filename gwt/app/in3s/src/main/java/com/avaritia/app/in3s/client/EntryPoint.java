// $Id$
package com.avaritia.app.in3s.client;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.avaritia.lib.console.client.Console;
import com.avaritia.lib.font.awesome.client.FontAwesomeBundle;
import com.avaritia.lib.font.mishmash.client.FontMishmashBundle;
import com.avaritia.lib.js.d3.client.D3Bundle;
import com.avaritia.lib.js.jsni.client.JsBundle;
import com.avaritia.lib.js.jsni.client.JsBundleLoader;
import com.avaritia.lib.js.sprintf.client.SprintfBundle;
import com.avaritia.lib.provider.client.ProviderBundle;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.event.shared.UmbrellaException;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.PhoneGapAvailableEvent;
import com.googlecode.gwtphonegap.client.PhoneGapAvailableHandler;
import com.googlecode.gwtphonegap.client.PhoneGapTimeoutEvent;
import com.googlecode.gwtphonegap.client.PhoneGapTimeoutHandler;
import com.googlecode.gwtphonegap.client.notification.AlertCallback;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.MGWTSettings.ViewPort;
import com.googlecode.mgwt.ui.client.util.SuperDevModeUtil;


public class EntryPoint implements com.google.gwt.core.client.EntryPoint {
   final static private Logger LOGGER = Logger.getLogger( EntryPoint.class.getName() );

   final static private Injector INJECTOR = Injector.INSTANCE; // Console needs this early


   static {
      FontAwesomeBundle.INSTANCE.fa().ensureInjected();
      FontMishmashBundle.INSTANCE.css().ensureInjected();
      AppCssBundle.INSTANCE.css().ensureInjected();
   }


   /**
    * Do basic UI setup and inject scripts in {@link #onModuleLoad()}.
    */
   @Override public void onModuleLoad() {
      exportEntryPoints( GWT.getModuleName(), GWT.getModuleBaseForStaticFiles().replace( GWT.getModuleName() + "/", "" ) );

      SuperDevModeUtil.showDevMode();

      ViewPort viewPort = new MGWTSettings.ViewPort();

      viewPort.setWidthToDeviceWidth();
      viewPort.setUserScaleAble( false ).setMinimumScale( 1.0 ).setMinimumScale( 1.0 ).setMaximumScale( 1.0 );

      MGWTSettings settings = new MGWTSettings();

      settings.setViewPort( viewPort );
      settings.setFullscreen( true );
      settings.setFixIOS71BodyBug( true );
      settings.setPreventScrolling( true );
      settings.setDisablePhoneNumberDetection( true );

      MGWT.applySettings( settings );

      // chain load js
      D3Bundle d3Bundle = GWT.create( D3Bundle.class );
      SprintfBundle sprintfBundle = GWT.create( SprintfBundle.class );
      ProviderBundle providerBundle = GWT.create( ProviderBundle.class );
      AppBundle in3sBundle = GWT.create( AppBundle.class );

      JsBundleLoader.chainLoadJsBundles( new Callback<Void, Exception>() {
         @Override public void onFailure( Exception reason ) {
            Console.error( GWT.getModuleName() + ".EntryPoint.onModuleLoad() chainLoadJsBundles failed: " + reason.getMessage() );
         }


         @Override public void onSuccess( Void result ) {
            main();
         }
      }
      , new JsBundle[][] {
           new JsBundle[] { d3Bundle, sprintfBundle, providerBundle }
         , new JsBundle[] { in3sBundle }
      } );
   }


   /**
    * Do work in {@link #main()}.
    */
   private void main() {
      final App app = INJECTOR.getApp();
      final PhoneGap phoneGap = INJECTOR.getPhoneGap();

      phoneGap.addHandler( new PhoneGapAvailableHandler() {
         @Override public void onPhoneGapAvailable( PhoneGapAvailableEvent event ) {
            GWT.setUncaughtExceptionHandler( new UncaughtExceptionHandler() {
               @Override public void onUncaughtException( Throwable e ) {
                  while ( e instanceof UmbrellaException ) {
                     e = ( (UmbrellaException) e ).getCauses().iterator().next();
                  }

                  String message = e.getMessage();

                  if ( message == null ) {
                     message = e.toString();
                  }

                  LOGGER.log( Level.SEVERE, message, e );

                  phoneGap.getNotification().alert( message, new AlertCallback() {
                     @Override public void onOkButtonClicked() {
                        // no-op
                     }
                  }, "!", "OK" ); // HARD-CODED
               }
            } );

            app.run( RootLayoutPanel.get() );
         }
      } );

      phoneGap.addHandler( new PhoneGapTimeoutHandler() {
         @Override public void onPhoneGapTimeout( PhoneGapTimeoutEvent event ) {
            Window.alert( "PhoneGap timed out!" );
         }
      } );

      phoneGap.initializePhoneGap();
   }


   // http://www.gwtproject.org/doc/latest/DevGuideCodingBasicsJSNI.html#calling
   native private void exportEntryPoints( String module, String docRoot ) /*-{
      try {
         $wnd.GWT = {
            docRoot : docRoot,
             module : module
         };
      } catch ( e ) {
         $wnd.alert( module + ".EntryPoint.exportEntryPoints(): " + e );
      }
   }-*/;
}
