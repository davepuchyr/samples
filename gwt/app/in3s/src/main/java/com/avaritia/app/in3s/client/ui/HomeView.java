// $Id$
package com.avaritia.app.in3s.client.ui;

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
public class HomeView extends Composite implements com.avaritia.app.in3s.client.presenter.HomeView {
   //final static private Logger LOGGER = Logger.getLogger( HomeView.class.getName() );

   interface Binder extends UiBinder<Widget, HomeView> {
   }

   @UiField HTMLPanel svgWrapper;


   private Presenter presenter;


   public HomeView() {
      //Console.log( GWT.getModuleName() + ".HomeView.HomeView(): about to initWidget()" );

      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );

      //Console.log( GWT.getModuleName() + ".HomeView.HomeView(): did initWidget()" );

      svgWrapper.getElement().setId( Document.get().createUniqueId() );
   }


   @Override public void setPresenter( Presenter presenter ) {
      if ( presenter != this.presenter && this.presenter != null ) this.presenter.stop();

      this.presenter = presenter;

      Scheduler.get().scheduleDeferred( new Command() { // the view must be attached before nativeRender()
         @Override public void execute() {
            //Console.log( GWT.getModuleName() + ".HomeView.setPresenter(): about to nativeRender()" );

            nativeRender( svgWrapper.getElement().getId() );

            //Console.log( GWT.getModuleName() + ".HomeView.setPresenter():      did nativeRender()" );
         }
      } );
   }


   @Override public void setTitle( String titled ) {
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

      function onForward() {
         instance.@com.avaritia.app.in3s.client.ui.HomeView::onNext()();
      }

      function onHelp() {
         instance.@com.avaritia.app.in3s.client.ui.HomeView::onHelp()();
      }

      var oArgs = {
            onForward : onForward,
               onHelp : onHelp,
         svgWrapperId : svgWrapperId
      };

      $wnd.app.home( oArgs );
   }-*/;
}
