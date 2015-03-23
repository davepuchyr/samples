// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.lib.console.client.Console;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;

/**
 * Debug version of {@link AppBundle}.
 */
public class AppBundleImplJs extends AppBundle {
   final static protected String[] scripts = new String[] { // HACK: order of injections
        app
      , home
      , d3
      , phonegap
      , help
   };


   public AppBundleImplJs() {
   }


   @Override public void inject( final Callback<Void, Exception> callback ) {
      Callback<Void, Exception> next = callback;
      int n = scripts.length;

      while ( --n >= 1 ) { // NOTE: 1, not 0...
         final String src = GWT.getModuleBaseURL() + scripts[n];
         final Callback<Void, Exception> fnext = next;
         final Callback<Void, Exception> current = new Callback<Void, Exception>() {
            @Override public void onFailure( Exception reason ) {
               callback.onFailure( reason );
            }

            @Override public void onSuccess( Void result ) {
               Console.log( GWT.getModuleName() + ".AppBundleImplJs.inject(): about to inject " + src );

               ScriptInjector.fromUrl( src ).setCallback( fnext ).setWindow( get$wnd() ).inject();
            }
         };

         next = current;
      }

      String src = GWT.getModuleBaseURL() + scripts[0];

      Console.log( GWT.getModuleName() + ".AppBundleImplJs.inject(): about to inject " + src );

      ScriptInjector.fromUrl( src ).setCallback( next ).setWindow( get$wnd() ).inject(); // ...kick it
   }
}
