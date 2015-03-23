// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.lib.ui.client.fontawesome.button.ButtonBack;
import com.avaritia.lib.ui.client.fontawesome.button.ButtonHelp;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.header.HeaderTitle;


@Singleton
public class SettingsView extends Composite implements com.avaritia.app.in3s.client.presenter.SettingsView {
   //final static private Logger LOGGER = Logger.getLogger( SettingsView.class.getName() );

   interface Binder extends UiBinder<Widget, SettingsView> {
   }

   @UiField HeaderTitle title;

   @UiField ButtonBack buttonBack;
   @UiField ButtonHelp buttonHelp;

   @UiField HTML latitude;
   @UiField HTML longitude;
   @UiField HTML altitude;
   @UiField HTML accuracy;
   @UiField HTML aa;
   @UiField HTML heading;
   @UiField HTML speed;
   @UiField HTML timestamp;

   @UiField Button startButton;

   private Presenter presenter;


   public SettingsView() {
      //Console.log( GWT.getModuleName() + ".SettingsView.SettingsView(): about to initWidget()" );

      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );

      //Console.log( GWT.getModuleName() + ".SettingsView.SettingsView(): did initWidget()" );
   }


   @Override public void setPresenter( Presenter presenter ) {
      if ( presenter != this.presenter && this.presenter != null ) this.presenter.stop();

      this.presenter = presenter;
   }


   @Override public void setTitle( String titled ) {
      title.setText( titled );
   }


   @UiHandler( "buttonBack" ) protected void onBack( TapEvent event ) {
      if ( presenter != null ) {
         presenter.onBack();
      }
   }


   @UiHandler( "buttonHelp" ) protected void onHelp( TapEvent event ) {
      if ( presenter != null ) {
         presenter.onHelp();
      }
   }


   @UiHandler( "startButton" ) protected void onStartButtonPressed( TapEvent event ) {
      if ( presenter != null ) {
         presenter.onStartStopButtonPressed();
      }
   }


   @Override public HasHTML getLatidute() {
      return latitude;
   }


   @Override public HasHTML getLongitude() {
      return longitude;
   }


   @Override public HasHTML getAltitude() {
      return altitude;
   }


   @Override public HasHTML getAccuracy() {
      return accuracy;
   }


   @Override public HasHTML getAltitudeAccuracy() {
      return aa;
   }


   @Override public HasHTML getHeading() {
      return heading;
   }


   @Override public HasHTML getTimeStamp() {
      return timestamp;
   }


   @Override public HasHTML getSpeed() {
      return speed;
   }


   @Override public HasText getStartStopButton() {
      return startButton;
   }
}
