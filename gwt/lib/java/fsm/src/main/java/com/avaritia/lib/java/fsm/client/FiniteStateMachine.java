// $Id$
package com.avaritia.lib.java.fsm.client;

import java.util.HashMap;
import java.util.Map;
import com.avaritia.lib.java.fsm.shared.FSM;
import com.avaritia.lib.java.fsm.shared.Logger;
import com.avaritia.lib.provider.client.Provider;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.inject.Inject;


abstract public class FiniteStateMachine<E extends Enum<E>> extends FSM<E> implements Historian, ValueChangeHandler<String> {
   abstract protected boolean isFacebookApp();

   abstract protected Event<E> getEventNavBack();
   abstract protected Event<E> getEventNavForward();
   abstract protected Event<E> getEventUnauthenticated();
   abstract protected Event<E> getEventUnauthorized();

   abstract protected Map<E, Event<E>> getEnum2EventMap();

   abstract protected Provider getProvider();

   abstract protected void setState0();
   abstract protected void setTransitions();


   @Inject static protected Logger LOGGER;

   @Inject private PlaceController placeController;

   protected FsmState<E> last;

   private Map<String, FsmState<E>> token2state = new HashMap<String, FsmState<E>>();

   private String token;

   private Map<E, Event<E>> enum2event;

   Event<E> evtNavBack;
   Event<E> evtNavForward;
   Event<E> evtUnauthenticated;
   Event<E> evtUnauthorized;


   public void bind() {
      // intercept browser button navigation
      History.addValueChangeHandler( this );

      evtNavBack         = getEventNavBack();
      evtNavForward      = getEventNavForward();
      evtUnauthenticated = getEventUnauthenticated();
      evtUnauthorized    = getEventUnauthorized();

      enum2event = getEnum2EventMap();

      setTransitions();
      setState0(); // kicks-off the fsm, so has to be after setTransitions()

      token2state.put( "", getCurrent() );
   }


   @Override public void deliver( FsmEvent<E> evt ) {
      assert( evt instanceof Event );

      ( (Event<?>) evt ).setHandled( false );

      super.deliver( evt );

      FsmState<E> current = getCurrent();

      if ( !token2state.containsKey( token ) ) {
         token2state.put( token, current );

         //LOGGER.warn( "'{}' => {}", token, current.getName() );
      }
   }


   @Override public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> valueChangeHandler ) {
      return History.addValueChangeHandler( valueChangeHandler );
   }


   @Override public String getToken() {
      return History.getToken();
   }


   @Override public void newItem( String token, boolean issueEvent ) {
      this.token = token;

      //LOGGER.warn( "newItem '{}'; current == {}", token, getCurrent().getName() );

      History.newItem( token, issueEvent );
   }


   /**
    * Synchronizes the fsm with browser state (hash), which is required on user navigation via the browser's back/forward buttons.
    * NOTE: This is by no means a robust solution because App.STATE values are only set on in app navigations.
    */
   @Override public void onValueChange( ValueChangeEvent<String> event ) {
      String value = event.getValue();
      FsmState<E> current = getCurrent();

      //LOGGER.warn( "value changed to '{}'; token == '{}'; current == {}", value, token, current.getName() );

      // possibly jump to the recorded state
      FsmState<E> state = token2state.get( value );

      if ( state == null ) {
         LOGGER.error( "FiniteStateMachine.onValueChange(): unknown token '{}'", value );
      } else if ( state != current ) {
         setCurrent( state );

         // TODO: sync App.STATE with token, ie most recent zingers might be 32,65 but we just jumped to 1024,69, so we need to fire the 1024,69 zingers request

         LOGGER.warn( "FiniteStateMachine.onValueChange(): state changed on token '{}'", value );
      } else {
         LOGGER.info( "FiniteStateMachine.onValueChange(): no state change on token '{}'", value );
      }
   }


   // handlers/guards
   abstract private class ToString {
      @Override public String toString() {
         return getClass().getSimpleName();
      }
   }


   abstract private class OnGoTo extends ToString implements FSM.EventHandler<E> {
      @Override public String toString() {
         return getClass().getSimpleName();
      }
   }


   protected class OnGoBack extends OnGoTo {
      public OnGoBack() {
      }


      @Override public void handleEvent( FsmEvent<E> evt ) {
         History.back();
      }
   }


   protected class OnGoToInvariant extends OnGoTo {
      private final Place place;


      public OnGoToInvariant( Place place ) {
         this.place = place;
      }


      @Override public void handleEvent( FsmEvent<E> evt ) {
         LOGGER.debug( "OnGoToInvariant.handleEvent(): place == {}; evt == {};", place.getClass().getSimpleName(), evt.getType().name() );

         placeController.goTo( place );
      }
   }


   protected class OnGoToVariant<P extends Place & HasToken> extends OnGoTo {
      private P place;


      public OnGoToVariant( P place ) {
         this.place = place;
      }


      @Override public void handleEvent( FsmEvent<E> evt ) {
         assert( evt instanceof Event && ( (Event<?>) evt ).getPayload() instanceof String );

         String token = (String) ( (Event<?>) evt ).getPayload();

         place.setToken( token );

         LOGGER.debug( "OnGoToVariant.handleEvent(): place == {}; evt == {}; token == {}", place.getClass().getSimpleName(), evt.getType().name(), token );

         placeController.goTo( place );
      }
   }


   protected class GuardNav extends ToString implements FSM.Guard<E> {
      protected final FiniteStateMachine<E> fsm;


      public GuardNav( FiniteStateMachine<E> fsm ) {
         this.fsm = fsm;
      }


      @Override public boolean accept( Transition<E> t ) {
         if ( enum2event.get( t.getTrigger() ).isHandled() ) return false; // short-circuit

         FsmState<E> current = fsm.getCurrent();

         if ( current == t.getFrom() && ( t.getTrigger() == evtNavForward.getType() || last == t.getTo() ) ) {
            LOGGER.debug( "GuardNav() PASS: current == {}; evt == {}; from == {}; to == {};", current.getName(), t.getTrigger().name(), t.getFrom().getName(), t.getTo().getName() );

            enum2event.get( t.getTrigger() ).setHandled( true );

            return true;
         }

         LOGGER.debug( "GuardNav() FAIL: current == {}; evt == {}; from == {}; to == {};", current.getName(), t.getTrigger().name(), t.getFrom().getName(), t.getTo().getName() );

         return false;
      }
   }


   protected class GuardIsFacebookApp extends GuardNav {
      public GuardIsFacebookApp( FiniteStateMachine<E> fsm ) {
         super( fsm );
      }


      @Override public boolean accept( Transition<E> t ) {
         if ( !isFacebookApp() ) return false; // short-circuit

         //LOGGER.trace( "GuardIsFacebookApp() {}: isFacebookApp == {};", App.isFacebookApp() ? "PASS" : "FAIL", App.isFacebookApp() );

         return super.accept( t );
      }
   }


   protected class GuardIsNotFacebookApp extends GuardNav {
      public GuardIsNotFacebookApp( FiniteStateMachine<E> fsm ) {
         super( fsm );
      }


      @Override public boolean accept( Transition<E> t ) {
         if ( isFacebookApp() ) return false; // short-circuit

         //LOGGER.trace( "GuardIsNotFacebookApp() {}: isFacebookApp == {};", App.isFacebookApp() ? "FAIL" : "PASS", App.isFacebookApp() );

         return super.accept( t );
      }
   }


   protected class GuardIsAuthenticated extends GuardNav {
      public GuardIsAuthenticated( FiniteStateMachine<E> fsm ) {
         super( fsm );
      }


      @Override public boolean accept( Transition<E> t ) {
         if ( enum2event.get( t.getTrigger() ).isHandled() ) return false; // short-circuit

         final Provider provider = getProvider();

         if ( provider == null ) {
            LOGGER.error( "GuardAuthenticated() FAIL: providerAdapter == null" );

            enum2event.get( t.getTrigger() ).setHandled( true );

            fsm.deliver( evtUnauthenticated );

            return false; // short-circuit
         } else if ( !provider.isAuthenticated() ) {
            LOGGER.debug( "GuardAuthenticated() FAIL: providerAdapter.isAuthenticated() == false" );

            enum2event.get( t.getTrigger() ).setHandled( true );

            final Command callback = new Command() {
               @Override public void execute() {
                  LOGGER.debug( "GuardAuthenticated().callback: adapter.isAuthenticated() == {};", provider.isAuthenticated() );

                  fsm.deliver( provider.isAuthenticated() ? evtNavForward : evtUnauthenticated );
               }
            };

            provider.authenticate( callback );

            return false; // short-circuit
         }

         LOGGER.debug( "GuardAuthenticated() PASS: providerAdapter.isAuthenticated() == true" );

         return super.accept( t );
      }
   }


   protected class GuardIsAuthorized extends GuardIsAuthenticated {
      public GuardIsAuthorized( FiniteStateMachine<E> fsm ) {
         super( fsm );
      }


      @Override public boolean accept( final Transition<E> t ) {
         if ( enum2event.get( t.getTrigger() ).isHandled() ) return false; // short-circuit

         final Provider provider = getProvider();

         if ( provider != null && !provider.isAuthorized() ) {
            LOGGER.debug( "GuardIsAuthorized() FAIL: providerAdapter.isAuthorized() == false" );

            enum2event.get( t.getTrigger() ).setHandled( true );

            final Command callback = new Command() {
               @Override public void execute() {
                  LOGGER.debug( "GuardIsAuthorized().callback: adapter.isAuthorized() == {};", provider.isAuthorized() );

                  fsm.deliver( provider.isAuthorized() ? evtNavForward : evtUnauthorized );
               }
            };

            provider.authorize( callback );

            return false; // short-circuit
         }

         LOGGER.debug( "GuardIsAuthorized() PASS: providerAdapter.isAuthorized() == true" );

         return super.accept( t );
      }
   }


   protected class SetLast implements Command {
      final FiniteStateMachine<E> fsm;


      public SetLast( FiniteStateMachine<E> fsm ) {
         this.fsm = fsm;
      }


      @Override public void execute() {
         FsmState<E> current = fsm.getCurrent();

         last = current;

         LOGGER.debug( "OnExit(): last == current == {};", last.getName() );
      }
   }
}
