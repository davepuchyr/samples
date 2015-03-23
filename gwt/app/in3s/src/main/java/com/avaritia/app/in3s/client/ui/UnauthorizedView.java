// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.lib.ui.client.fontawesome.button.ButtonBack;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.ui.client.widget.header.HeaderTitle;


@Singleton
public class UnauthorizedView extends Composite implements com.avaritia.app.in3s.client.presenter.UnauthorizedView {
   //final static private Logger LOGGER = Logger.getLogger( UnauthorizedView.class.getName() );

   interface Binder extends UiBinder<Widget, UnauthorizedView> {
   }

   @UiField HeaderTitle title;

   @UiField ButtonBack buttonBack;

   private Presenter presenter;


   public UnauthorizedView() {
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
}
