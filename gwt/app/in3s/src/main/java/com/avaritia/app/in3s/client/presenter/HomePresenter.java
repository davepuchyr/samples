// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.avaritia.app.in3s.client.FiniteStateMachine;
import com.avaritia.app.in3s.client.PlaceHistoryMapper;
import com.avaritia.app.in3s.client.place.DelimitedPlace;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;


@Singleton
public class HomePresenter extends PresenterImpl implements HomeView.Presenter {
   @Inject @Named( "com.avaritia.app.in3s.client.presenter.HomePresenter.title" ) private String title;

   @Inject private HomeView view;


   public HomePresenter() {
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
      return title + DelimitedPlace.DELIMITER + PlaceHistoryMapper.PREFIX_HOME;
   }


   @Override public void setSelected( String i ) {
      if ( eventBus != null ) {
         fsm.deliver( FiniteStateMachine.NAV_BACK.setPayload( i ) );
      }
   }
}
