// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.lib.console.client.Console;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.googlecode.gwtphonegap.client.geolocation.Coordinates;
import com.googlecode.gwtphonegap.client.geolocation.Position;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.button.Button;


@Singleton
public class PhoneGapView extends Composite implements com.avaritia.app.in3s.client.presenter.PhoneGapView {
   //static private final Logger LOGGER = Logger.getLogger( PhoneGapView.class.getName() );

   interface Binder extends UiBinder<Widget, PhoneGapView> {
   }

   @UiField HTMLPanel svgWrapper;

   @UiField InlineLabel altitude;
   @UiField InlineLabel latitude;
   @UiField InlineLabel longitude;

   @UiField Button toggleGPS;

   private Presenter presenter;


   public PhoneGapView() {
      //Console.log( GWT.getModuleName() + ".PhoneGapView.PhoneGapView(): about to initWidget()" );

      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );

      //Console.log( GWT.getModuleName() + ".PhoneGapView.PhoneGapView(): did initWidget()" );

      svgWrapper.getElement().setId( Document.get().createUniqueId() );
   }


   @Override public void setPresenter( Presenter presenter ) {
      if ( presenter != this.presenter && this.presenter != null ) this.presenter.stop();

      this.presenter = presenter;

      Scheduler.get().scheduleDeferred( new Command() { // the view must be attached before nativeRender()
         @Override public void execute() {
            //Console.log( GWT.getModuleName() + ".PhoneGapView.setPresenter(): about to nativeRender()" );

            nativeRender( svgWrapper.getElement().getId() );

            //Console.log( GWT.getModuleName() + ".PhoneGapView.setPresenter():      did nativeRender()" );
         }
      } );
   }


   @Override public void setTitle( String titled ) {
      Console.log( GWT.getModuleName() + ".PhoneGapView.setTitle(): title == " + titled );
   }


   @UiHandler( "toggleGPS" ) protected void onToggleGPS( TapEvent event ) {
      if ( presenter != null ) {
         presenter.onToggleGPS();
      }
   }


   @Override public void setPosition( Position position ) {
      if ( position != null ) {
         Coordinates coordinates = position.getCoordinates();

         altitude.setText( "" + coordinates.getAltitude() );
         latitude.setText( "" + coordinates.getLatitude() );
         longitude.setText( "" + coordinates.getLongitude() );
      } else {
         altitude.setText( "" );
         latitude.setText( "" );
         longitude.setText( "" );
      }
   }


   @Override public void setToggleGPSText( String text ) {
      toggleGPS.setText( text );
   }


   private void onBack() {
      if ( presenter != null ) {
         presenter.onBack();
      }
   }


   private void onHelp() {
      if ( presenter != null ) {
         presenter.onHelp();
      }
   }


   private void onNext() {
      if ( presenter != null ) {
         presenter.onForward();
      }
   }


   native private void nativeRender( String svgWrapperId ) /*-{
      var instance = this;

      function onBack() {
         instance.@com.avaritia.app.in3s.client.ui.PhoneGapView::onBack()();
      }

      function onForward() {
         instance.@com.avaritia.app.in3s.client.ui.PhoneGapView::onNext()();
      }

      function onHelp() {
         instance.@com.avaritia.app.in3s.client.ui.PhoneGapView::onHelp()();
      }

      var oArgs = {
               onBack : onBack,
               onHelp : onHelp,
            onForward : onForward,
         svgWrapperId : svgWrapperId
      };

      $wnd.app.phonegap( oArgs );
   }-*/;
}
