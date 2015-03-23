// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.lib.ui.client.fontawesome.button.ButtonBack;
import com.avaritia.lib.ui.client.fontawesome.button.ButtonNext;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.header.HeaderTitle;


@Singleton
public class AuthenticatedView extends Composite implements com.avaritia.app.in3s.client.presenter.AuthenticatedView {
   //final static private Logger LOGGER = Logger.getLogger( UnauthenticatedView.class.getName() );

   interface Binder extends UiBinder<Widget, AuthenticatedView> {
   }

   @UiField HeaderTitle title;

   @UiField ButtonBack buttonBack;
   @UiField ButtonNext buttonNext;

   @UiField Button getMyProviderId;


   private Presenter presenter;


   public AuthenticatedView() {
      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );
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


   @UiHandler( "buttonNext" ) protected void onNext( TapEvent event ) {
      if ( presenter != null ) {
         presenter.onForward();
      }
   }


   @UiHandler( "getMyProviderId" ) protected void onGetMyProviderId( ClickEvent event ) {
      if ( presenter != null ) {
         presenter.onGetMyIdFromProvider();
      }
   }
}
