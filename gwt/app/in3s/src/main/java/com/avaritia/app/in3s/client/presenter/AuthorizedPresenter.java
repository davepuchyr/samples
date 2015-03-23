// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.avaritia.app.in3s.client.place.DelimitedPlace;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;


@Singleton
public class AuthorizedPresenter extends PresenterImpl implements AuthorizedView.Presenter {
   @Inject @Named( "com.avaritia.app.in3s.client.presenter.AuthorizedPresenter.title" ) private String title;

   @Inject private AuthorizedView view;


   @Override public void start( final AcceptsOneWidget panel, final EventBus eventBus ) {
      this.eventBus = eventBus;

      view.setPresenter( this );
      view.setTitle( title );

      panel.setWidget( view );
   }


   @Override public void stop() {
      eventBus = null;
   }


   @Override public String getHelpToken() {
      return title + DelimitedPlace.DELIMITER + PlaceHistoryMapper.PREFIX_AUTHORIZED;
   }
}
