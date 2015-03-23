// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.lib.js.jsni.client.builders.JsniBundle;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.shared.GWT;

/**
 * Release version of {@link AppBundle}.
 */
public class AppBundleImplJsni extends AppBundle {
   public interface AppJsSourceBundle extends JsniBundle {
      @JsSource( { // HACK: order of injections
           AppBundle.app
         , AppBundle.home
         , AppBundle.d3
         , AppBundle.phonegap
         , AppBundle.help
      } )
      public void rebind();
   }


   public AppBundleImplJsni() {
   }


   @Override public void inject( Callback<Void, Exception> callback ) {
      AppJsSourceBundle  app  = GWT.create( AppJsSourceBundle.class );

      app.rebind();

      if ( callback != null ) callback.onSuccess( null );
   }
}
