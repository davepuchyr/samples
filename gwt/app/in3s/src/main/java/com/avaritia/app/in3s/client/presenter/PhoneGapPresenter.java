// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.avaritia.app.in3s.client.place.DelimitedPlace;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwtphonegap.client.PhoneGap;
import com.googlecode.gwtphonegap.client.geolocation.GeolocationCallback;
import com.googlecode.gwtphonegap.client.geolocation.GeolocationOptions;
import com.googlecode.gwtphonegap.client.geolocation.GeolocationWatcher;
import com.googlecode.gwtphonegap.client.geolocation.Position;
import com.googlecode.gwtphonegap.client.geolocation.PositionError;


@Singleton
public class PhoneGapPresenter extends PresenterImpl implements PhoneGapView.Presenter {
   @Inject @Named( "com.avaritia.app.in3s.client.presenter.PhoneGapPresenter.title" ) private String title;

   @Inject private PhoneGapView view;

   @Inject private PhoneGap phoneGap;

   private GeolocationWatcher watcher;


   public PhoneGapPresenter() {
   }


   @Override public void start( AcceptsOneWidget panel, EventBus eventBus ) {
      this.eventBus = eventBus;

      view.setPresenter( this );

      panel.setWidget( view );
   }


   @Override public void stop() {
      eventBus = null;

      if ( watcher != null ) {
         phoneGap.getGeolocation().clearWatch( watcher );

         watcher = null;
      }
   }


   @Override public String getHelpToken() {
      return title + DelimitedPlace.DELIMITER + PlaceHistoryMapper.PREFIX_PHONEGAP;
   }


   @Override public void onToggleGPS() {
      if ( watcher == null ) {
         GeolocationOptions options = new GeolocationOptions();

         watcher = phoneGap.getGeolocation().watchPosition( options, new GeolocationCallback() {
            @Override public void onSuccess( Position position ) {
               view.setPosition( position ) ;
            }


            @Override public void onFailure( PositionError error ) {
               switch ( error.getCode() ) {
                  case PositionError.PERMISSION_DENIED:
                     Window.alert( "no permission - stopping watcher" );
                     break;
                  case PositionError.POSITION_UNAVAILABLE:
                     Window.alert( "unavailble" );
                     break;
                  case PositionError.TIMEOUT:
                     Window.alert( "timeout" );
                     break;
               }

               if ( watcher != null ) {
                  phoneGap.getGeolocation().clearWatch( watcher );

                  watcher = null;
               }

               view.setToggleGPSText( "Start" );
            }
         } );

         view.setToggleGPSText( "Stop" );
      } else {
         phoneGap.getGeolocation().clearWatch( watcher );

         watcher = null;

         view.setPosition( null );
         view.setToggleGPSText( "Start" );
      }
   }
}
