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
public class D3Presenter extends PresenterImpl implements D3View.Presenter {
   @Inject @Named( "com.avaritia.app.in3s.client.presenter.D3Presenter.title" ) private String title;

   @Inject private D3View view;

   public D3Presenter() {
   }


   @Override public void start( AcceptsOneWidget panel, EventBus eventBus ) {
      this.eventBus = eventBus;

      view.setPresenter( this );

      panel.setWidget( view );
   }


   @Override public void stop() {
      eventBus = null;
   }


   @Override public String getHelpToken() {
      return title + DelimitedPlace.DELIMITER + PlaceHistoryMapper.PREFIX_D3;
   }
}
