// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.google.gwt.user.client.ui.IsWidget;


public interface AuthorizedView extends IsWidget {
   public interface Presenter extends com.avaritia.app.in3s.client.presenter.Presenter {
   }

   void setPresenter( Presenter presenter );

   void setTitle( String title );
}

