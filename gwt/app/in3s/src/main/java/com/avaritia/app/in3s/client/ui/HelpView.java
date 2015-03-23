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
import com.googlecode.mgwt.ui.client.widget.panel.Panel;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;


@Singleton
public class HelpView extends Composite implements com.avaritia.app.in3s.client.presenter.HelpView {
   //final static private Logger LOGGER = Logger.getLogger( HelpView.class.getName() );

   interface Binder extends UiBinder<Widget, HelpView> {
   }

   @UiField ScrollPanel scrollPanel;
   @UiField Panel panel;

   private Presenter presenter;

   private boolean rendered = false;


   public HelpView() {
      //Console.log( GWT.getModuleName() + ".HelpView.HelpView(): about to initWidget()" );

      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );

      //Console.log( GWT.getModuleName() + ".HelpView.HelpView(): did initWidget()" );

      scrollPanel.getElement().setId( Document.get().createUniqueId() );
   }


   @Override public void setPresenter( Presenter presenter ) {
      if ( presenter != this.presenter && this.presenter != null ) this.presenter.stop();

      this.presenter = presenter;

      if ( rendered ) return; // short-circuit

      final HelpView self = this;

      Scheduler.get().scheduleDeferred( new Command() { // the view must be attached before nativeRender()
         @Override public void execute() {
            //Console.log( GWT.getModuleName() + ".HelpView.setPresenter(): about to nativeRender()" );

            nativeRender( scrollPanel.getElement().getId() );

            self.rendered = true;

            //Console.log( GWT.getModuleName() + ".HelpView.setPresenter():      did nativeRender()" );
         }
      } );
   }


   @Override public void setTitle( String titled ) {
   }


   @Override public void setBody( String html ) {
      panel.clear();

      HTMLPanel htmlPanel = new HTMLPanel( html );

      panel.add( htmlPanel );
   }


   private void onBack() {
      if ( presenter != null ) {
         presenter.onBack();
      }
   }


   native private void nativeRender( String scrollPanelId ) /*-{
      var instance = this;

      function onBack() {
         instance.@com.avaritia.app.in3s.client.ui.HelpView::onBack()();
      }

      var oArgs = {
                onBack : onBack,
         scrollPanelId : scrollPanelId
      };

      $wnd.app.help( oArgs );
   }-*/;
}
