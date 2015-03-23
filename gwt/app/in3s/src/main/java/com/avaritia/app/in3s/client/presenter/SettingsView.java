// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;


public interface SettingsView extends IsWidget {
   public interface Presenter extends com.avaritia.app.in3s.client.presenter.Presenter {
      public void onStartStopButtonPressed();

      public void setLocale( String localeName );
   }


   void setPresenter( Presenter presenter );

   void setTitle( String title );

   public HasHTML getLatidute();

   public HasHTML getLongitude();

   public HasHTML getAltitude();

   public HasHTML getAccuracy();

   public HasHTML getAltitudeAccuracy();

   public HasHTML getHeading();

   public HasHTML getTimeStamp();

   public HasHTML getSpeed();

   public HasText getStartStopButton();
}

