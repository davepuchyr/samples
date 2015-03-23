// $Id$
package com.avaritia.app.in3s.client.presenter;

import java.util.Date;
import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.avaritia.app.in3s.client.place.DelimitedPlace;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
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
public class SettingsPresenter extends PresenterImpl implements SettingsView.Presenter {
   @Inject @Named( "com.avaritia.app.in3s.client.presenter.SettingsPresenter.title" ) private String title;

   @Inject private SettingsView view;

   @Inject private PhoneGap phoneGap;

   private GeolocationWatcher watcher;


    public SettingsPresenter() {
   }


   @Override public void start( AcceptsOneWidget panel, EventBus eventBus ) {
      this.eventBus = eventBus;

      view.setPresenter( this );
      view.setTitle( title );

      clearGeolocationUI();

      panel.setWidget( view );
   }


   @Override public void stop() {
      if ( watcher != null ) {
         phoneGap.getGeolocation().clearWatch( watcher );

         watcher = null;
         eventBus = null;
      }
   }


   @Override public String getHelpToken() {
      return title + DelimitedPlace.DELIMITER + PlaceHistoryMapper.PREFIX_SETTINGS;
   }


   @Override public void onStartStopButtonPressed() {
      if ( watcher == null ) {
         GeolocationOptions options = new GeolocationOptions();

         watcher = phoneGap.getGeolocation().watchPosition( options, new GeolocationCallback() {
            @Override public void onSuccess( Position position ) {
               view.getAccuracy().setText( "" + position.getCoordinates().getAccuracy() );
               view.getAltitude().setText( "" + position.getCoordinates().getAltitude() );
               view.getAltitudeAccuracy().setText( "" + position.getCoordinates().getAltitudeAccuracy() );
               view.getHeading().setText( "" + position.getCoordinates().getHeading() );
               view.getLatidute().setText( "" + position.getCoordinates().getLatitude() );
               view.getLongitude().setText( "" + position.getCoordinates().getLongitude() );
               view.getSpeed().setText( "" + position.getCoordinates().getSpeed() );
               view.getTimeStamp().setText( "" + position.getTimeStamp() );
            }


            @Override public void onFailure( PositionError error ) {
               switch ( error.getCode() ) {
                  case PositionError.PERMISSION_DENIED:
                     Window.alert( "no permission - stoping watcher" );

                     break;
                  case PositionError.POSITION_UNAVAILABLE:
                     Window.alert( "unavaible" );
                     break;
                  case PositionError.TIMEOUT:
                     Window.alert( "timeout" );
                     break;
                  default:
                     break;
               }

               if ( watcher != null ) {
                  phoneGap.getGeolocation().clearWatch( watcher );

                  watcher = null;

                  view.getStartStopButton().setText( "Start" );
               }
            }
         } );

         view.getStartStopButton().setText( "Stop" );
      } else {
         phoneGap.getGeolocation().clearWatch( watcher );

         watcher = null;

         clearGeolocationUI();
      }
   }


   private void clearGeolocationUI() {
      view.getStartStopButton().setText( "Start" );

      view.getAccuracy().setText( "" );
      view.getAltitude().setText( "" );
      view.getAltitudeAccuracy().setText( "" );
      view.getHeading().setText( "" );
      view.getLatidute().setText( "" );
      view.getLongitude().setText( "" );
      view.getTimeStamp().setText( "" );
   }


   @Override public void setLocale( String localeName ) {
      stop();

      Date now = new Date();
      Date expires = new Date( now.getTime() + 1000L * 60 * 60 * 24 * 365 );

      Cookies.setCookie( LocaleInfo.getLocaleCookieName(), localeName, expires );

      Window.Location.reload();
   }
}
