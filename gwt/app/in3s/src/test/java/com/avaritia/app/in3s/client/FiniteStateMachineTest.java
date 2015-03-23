// $Id$
package com.avaritia.app.in3s.client;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.avaritia.app.in3s.client.FiniteStateMachine.EVENT;
import com.avaritia.lib.java.fsm.client.HasToken;
import com.avaritia.lib.java.fsm.shared.FSM;
import com.avaritia.lib.java.fsm.shared.FSM.FsmState;
import com.avaritia.lib.java.fsm.shared.Logger;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.impl.HistoryImpl;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;


@RunWith( GwtMockitoTestRunner.class )
public class FiniteStateMachineTest {
   static private Injector injector;
   static private FiniteStateMachine fsm;


   /**
    * http://stackoverflow.com/questions/12293388/test-case-for-gwt-mvp-with-activities-and-places-and-gin-using-mockito: As a side
    * note, this code probably makes too much use of mocking: why not using the real PlaceController (and spy()ing its goTo(Place)
    * method) and a SimpleEventBus or CountingEventBus?
    */
   static public class FakePlaceController extends PlaceController {
      @Inject public FakePlaceController( EventBus eventBus ) {
         super( eventBus );
      }


      @Override public void goTo( Place newPlace ) {
         String place = newPlace.getClass().getSimpleName();

         if ( newPlace instanceof HasToken ) {
            place = place + "." + ( (HasToken) newPlace ).getToken();
         }

         System.out.println( "FakePlaceController.goTo( " + place + " )" );
      }
   }


   static public class FakeHistoryImpl extends HistoryImpl {
      @Override public boolean init() { // this doesn't remove the warning due to static private delegate; don't know how to deal with that as of 2014.10.27
         return true;
      };
   }


   @Before public void before() {
      if ( injector == null ) {
         injector = Guice.createInjector( new AbstractModule() {
            @Override protected void configure() {
               bind( EventBus.class ).to( SimpleEventBus.class );
               bind( Logger.class ).to( com.avaritia.lib.java.fsm.server.LoggerImpl.class );
               bind( HistoryImpl.class ).to( FakeHistoryImpl.class );
               bind( PlaceController.class ).to( FakePlaceController.class );

               requestStaticInjection( FiniteStateMachine.class );
               requestStaticInjection( FSM.class );
            }
         } );
      }

      fsm = injector.getInstance( FiniteStateMachine.class );

      fsm.bind();
   }


   private FsmState<EVENT> getState0() {
      return fsm.stateHome;
   }


   private void assertState0() {
      assertEquals( getState0(), fsm.getCurrent() );
   }


   @Test public void navBackFromState0() {
      assertState0();

      fsm.deliver( FiniteStateMachine.NAV_BACK );

      assertEquals( getState0(), fsm.getCurrent() );
   }
}
