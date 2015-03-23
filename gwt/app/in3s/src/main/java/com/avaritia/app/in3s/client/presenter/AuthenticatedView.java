// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.google.gwt.user.client.ui.IsWidget;


public interface AuthenticatedView extends IsWidget {
   public interface Presenter extends com.avaritia.app.in3s.client.presenter.Presenter {
      public void onGetMyIdFromProvider();
   }

   void setPresenter( Presenter presenter );

   void setTitle( String title );
}

