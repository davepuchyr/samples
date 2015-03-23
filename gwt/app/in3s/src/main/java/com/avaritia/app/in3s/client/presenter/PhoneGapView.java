// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.google.gwt.user.client.ui.IsWidget;
import com.googlecode.gwtphonegap.client.geolocation.Position;


public interface PhoneGapView extends IsWidget {
   public interface Presenter extends com.avaritia.app.in3s.client.presenter.Presenter {
      public void onToggleGPS();
   }

   void setPosition( Position position );

   void setPresenter( Presenter presenter );

   void setToggleGPSText( String text );
}

