// $Id$
package com.avaritia.app.in3s.client.provider;

import com.avaritia.lib.console.client.Console;
import com.avaritia.lib.provider.client.Facebook;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An app specific adapter for {@link ProviderAdapter}.
 */
@Singleton
public class FacebookAdapter extends ProviderAdapter {
   @Inject
   public FacebookAdapter( Facebook facebook ) {
      super( facebook );

      Console.log( GWT.getModuleName() + ".FacebookAdapter.FacebookAdapter(): about to nativeFacebook()" );

      nativeFacebook();

      Console.log( GWT.getModuleName() + ".FacebookAdapter.FacebookAdapter():      did nativeFacebook()" );
   }


   @Override public String getAuthenticationScope() {
      return "public_profile,user_friends"; // HARD-CODED
   }


   @Override public String getAuthorizationScope() {
      return getAuthenticationScope() + ",publish_actions"; // HARD-CODED
   }


   @Override public void getUser() {
      nativeGetMe();
   }


   private void onMe( String id ) {
      setMyProviderId( id );
   }



   native private void nativeFacebook() /*-{
      var instance = this;

      // first provider...
      function onAuthenticate() {
         instance.@com.avaritia.app.in3s.client.provider.FacebookAdapter::onAuthenticate()();
      }

      function onAuthorize() {
         instance.@com.avaritia.app.in3s.client.provider.FacebookAdapter::onAuthorize()();
      }

      var oArgs = {
         onAuthenticate : onAuthenticate,
         onAuthorize    : onAuthorize
      };

      $wnd.provider.facebook( oArgs );

      // ...then app
      function onMe( o ) {
         instance.@com.avaritia.app.in3s.client.provider.FacebookAdapter::onMe(Ljava/lang/String;)( o.id );
      }

      oArgs = {
         onMe : onMe
      };

      $wnd.app.facebook( oArgs );
   }-*/;


   native private void nativeGetMe() /*-{
      $wnd.app.facebook.me();
   }-*/;
}
