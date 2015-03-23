// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.lib.console.client.Console;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;


@Singleton
public class D3View extends Composite implements com.avaritia.app.in3s.client.presenter.D3View {
   //static private final Logger LOGGER = Logger.getLogger( D3View.class.getName() );

   interface Binder extends UiBinder<Widget, D3View> {
   }

   @UiField HTMLPanel svgWrapper;


   private Presenter presenter;


   public D3View() {
      //Console.log( GWT.getModuleName() + ".D3View.D3View(): about to initWidget()" );

      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );

      //Console.log( GWT.getModuleName() + ".D3View.D3View(): did initWidget()" );

      svgWrapper.getElement().setId( Document.get().createUniqueId() );
   }


   @Override public void setPresenter( Presenter presenter ) {
      if ( presenter != this.presenter && this.presenter != null ) this.presenter.stop();

      this.presenter = presenter;

      Scheduler.get().scheduleDeferred( new Command() { // the view must be attached before nativeRender()
         @Override public void execute() {
            //Console.log( GWT.getModuleName() + ".D3View.setPresenter(): about to nativeRender()" );

            nativeRender( svgWrapper.getElement().getId() );

            //Console.log( GWT.getModuleName() + ".D3View.setPresenter():      did nativeRender()" );
         }
      } );
   }


   @Override public void setTitle( String titled ) {
      Console.log( GWT.getModuleName() + ".D3View.setTitle(): title == " + titled );
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


   native private void nativeRender( String svgWrapperId ) /*-{
      var instance = this;

      function onBack() {
         instance.@com.avaritia.app.in3s.client.ui.D3View::onBack()();
      }

      function onHelp() {
         instance.@com.avaritia.app.in3s.client.ui.D3View::onHelp()();
      }

      var oArgs = {
               onBack : onBack,
               onHelp : onHelp,
         svgWrapperId : svgWrapperId
      };

      $wnd.app.d3( oArgs );
   }-*/;
}
