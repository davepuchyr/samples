// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.google.gwt.user.client.ui.IsWidget;


public interface HelpView extends IsWidget {
   public interface Presenter extends com.avaritia.app.in3s.client.presenter.Presenter {
      void onHome();
   }

   void setPresenter( Presenter presenter );

   void setTitle( String title );

   void setBody( String html );
}

