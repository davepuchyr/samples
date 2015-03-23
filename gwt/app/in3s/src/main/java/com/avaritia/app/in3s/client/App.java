// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.app.in3s.client.place.HomePlace;
import com.avaritia.lib.place.shared.PlaceController;
import com.avaritia.lib.provider.client.ProviderEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


@Singleton
public class App {
   //static private final Logger LOGGER = Logger.getLogger( App.class.getName() );

   @Inject private FiniteStateMachine fsm;

   @Inject private AppView view;

   @Inject private EventBus eventBus;

   @Inject private PlaceController placeController;

   @Inject private PlaceHistoryHandler historyHandler;


   public void run( HasWidgets.ForIsWidget parentView ) {
      // bind injected objects
      view.bind();

      parentView.add( view );

      fsm.bind();

      // register event handlers; fsm manages navigation
      eventBus.addHandler( ProviderEvent.TYPE, new ProviderEvent.Handler() {
         @Override public void onEvent( ProviderEvent event ) {
            Window.alert( event.getProvider() == null ? "null" : event.getProvider().toString() ); // dmjp
         }
      } );

      // setup history
      historyHandler.register( placeController, eventBus, HomePlace.INSTANCE ); // NOTE: default place

      // go to initial place
      placeController.goTo( HomePlace.INSTANCE ); // HARD-CODED in conjunction with FiniteStateMachine.setState0()
   }


   public static boolean isFacebookApp() {
      return false;
   }
}
