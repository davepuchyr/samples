package com.netmorpher.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Publisher implements EntryPoint {
   public void onModuleLoad() {
      ApplicationFacade facade = ApplicationFacade.getInstance();

      RootPanel.get().add( facade.getWidget() );
      Window.addResizeHandler( facade );
      facade.onResize( null );
   }
}
