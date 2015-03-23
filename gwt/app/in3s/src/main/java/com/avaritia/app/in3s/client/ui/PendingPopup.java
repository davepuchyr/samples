package com.avaritia.app.in3s.client.ui;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Singleton;
import com.googlecode.mgwt.ui.client.widget.progress.ProgressIndicator;


@Singleton
public class PendingPopup extends PopupPanel {
   ProgressIndicator progressIndicator = new ProgressIndicator();

   public PendingPopup() {
      progressIndicator.getElement().setAttribute( "style", "margin: 0 auto; width: 100px;" );

      setWidget( progressIndicator );
      setGlassEnabled( true );
      //setAnimationEnabled( true );
      setAutoHideOnHistoryEventsEnabled( true );
      setModal( true );
      center();
      hide( true );
   }
}
