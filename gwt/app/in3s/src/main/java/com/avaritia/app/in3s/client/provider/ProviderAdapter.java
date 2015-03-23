// $Id$
package com.avaritia.app.in3s.client.provider;

import com.avaritia.lib.provider.client.ProviderDelegate;
import com.avaritia.lib.provider.client.ProviderImpl;
import com.google.gwt.user.client.Command;


/**
 * An app specific adapter for {@link ProviderImpl}.
 */
abstract public class ProviderAdapter extends ProviderImpl {
   private Command onMyProviderId;

   private String myProviderId;

   /* dmjp
   // oauth test
   String apiKey = "588878157889778"; // ZingerZapp
   String apiSecret = "945c1b9494e5355e65c73c0ba7446fe4";
   FacebookApi api = GWT.create( FacebookApi.class );
   OAuthService service = new ServiceBuilder()
      .provider( api )
      .apiKey( apiKey )
      .apiSecret( apiSecret )
      .callback( "https://www.facebook.com/connect/login_success.html" )
      .build();
   String url = service.getAuthorizationUrl( null );
   Console.log( "url == " + url );
   */
   // ~dmjp


   public ProviderAdapter( ProviderDelegate delegate ) {
      super( delegate );
   }


   public void getMyProviderId( Command callback ) {
      onMyProviderId = callback;

      getUser();
   }


   public String getMyProviderId() {
      //if ( onMyProviderId == null ) blah blah

      return myProviderId;
   }


   protected void setMyProviderId( String myProviderId ) {
      this.myProviderId = myProviderId;

      onMyProviderId.execute();
   }
}
