// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.google.gwt.user.client.ui.IsWidget;


public interface HomeView extends IsWidget {
   public interface Presenter extends com.avaritia.app.in3s.client.presenter.Presenter {
      public void setSelected( String i );
   }


   void setPresenter( Presenter presenter );
}

