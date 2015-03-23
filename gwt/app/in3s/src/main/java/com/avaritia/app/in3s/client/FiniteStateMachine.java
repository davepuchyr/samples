// $Id$
package com.avaritia.app.in3s.client;

import java.util.HashMap;
import java.util.Map;
import com.avaritia.app.in3s.client.place.AuthenticatedPlace;
import com.avaritia.app.in3s.client.place.AuthorizedPlace;
import com.avaritia.app.in3s.client.place.D3Place;
import com.avaritia.app.in3s.client.place.HelpPlace;
import com.avaritia.app.in3s.client.place.HomePlace;
import com.avaritia.app.in3s.client.place.PhoneGapPlace;
import com.avaritia.app.in3s.client.place.SettingsPlace;
import com.avaritia.app.in3s.client.place.UnauthenticatedPlace;
import com.avaritia.app.in3s.client.place.UnauthorizedPlace;
import com.avaritia.lib.java.fsm.client.Event;
import com.avaritia.lib.java.fsm.client.EventImpl;
import com.avaritia.lib.provider.client.Provider;
import com.google.gwt.user.client.Window;
import com.google.inject.Singleton;


@Singleton
public class FiniteStateMachine extends com.avaritia.lib.java.fsm.client.FiniteStateMachine<FiniteStateMachine.EVENT> {
   public enum EVENT {
        D3
      , HELP
      , HOME
      , NAV_BACK
      , NAV_FORWARD
      , PHONEGAP
      , SETTINGS
      , UNAUTHENTICATED
      , UNAUTHORIZED
      ;
   }


   // registrar for events
   static private final Map<EVENT, Event<EVENT>> enum2event = new HashMap<EVENT, Event<EVENT>>();

   static private EventImpl<EVENT> register( EventImpl<EVENT> impl ) {
      enum2event.put( impl.getType(), impl );

      return impl;
   }


   // register events
   static public final EventImpl<EVENT> D3              = register( new EventImpl<EVENT>( EVENT.D3 ) );
   static public final EventImpl<EVENT> HELP            = register( new EventImpl<EVENT>( EVENT.HELP ) );
   static public final EventImpl<EVENT> HOME            = register( new EventImpl<EVENT>( EVENT.HOME ) );
   static public final EventImpl<EVENT> NAV_BACK        = register( new EventImpl<EVENT>( EVENT.NAV_BACK ) );
   static public final EventImpl<EVENT> NAV_FORWARD     = register( new EventImpl<EVENT>( EVENT.NAV_FORWARD ) );
   static public final EventImpl<EVENT> PHONEGAP        = register( new EventImpl<EVENT>( EVENT.PHONEGAP ) );
   static public final EventImpl<EVENT> SETTINGS        = register( new EventImpl<EVENT>( EVENT.SETTINGS ) );
   static public final EventImpl<EVENT> UNAUTHENTICATED = register( new EventImpl<EVENT>( EVENT.UNAUTHENTICATED ) );
   static public final EventImpl<EVENT> UNAUTHORIZED    = register( new EventImpl<EVENT>( EVENT.UNAUTHORIZED ) );


   // package visibility for unit testing
   protected FsmState<EVENT> stateAuthenticated;
   protected FsmState<EVENT> stateAuthorized;
   protected FsmState<EVENT> stateD3;
   protected FsmState<EVENT> stateHelp;
   protected FsmState<EVENT> stateHome;
   protected FsmState<EVENT> statePhoneGap;
   protected FsmState<EVENT> stateSettings;
   protected FsmState<EVENT> stateUnauthenticated;
   protected FsmState<EVENT> stateUnauthorized;


   @Override protected boolean isFacebookApp() {
      return App.isFacebookApp();
   }


   @Override protected Event<EVENT> getEventNavBack() {
      return NAV_BACK;
   }


   @Override protected Event<EVENT> getEventNavForward() {
      return NAV_FORWARD;
   }


   @Override protected Event<EVENT> getEventUnauthenticated() {
      return UNAUTHENTICATED;
   }


   @Override protected Event<EVENT> getEventUnauthorized() {
      return UNAUTHORIZED;
   }


   @Override protected Map<EVENT, Event<EVENT>> getEnum2EventMap() {
      return enum2event;
   }


   @Override protected Provider getProvider() {
      Window.alert( "dmjp: getProvider()" ); // dmjp

      return null; // dmjp
      //return App.getState().getProvider();
   }


   @Override protected void setState0() {
      last = null;

      setCurrent( stateHome ); // HARD-CODED in conjunction with App.runs()'s initial place
   }


   @Override protected void setTransitions() {
      // enters/exits/default handlers
      SetLast onExit = new SetLast( this );

      // states
      stateAuthenticated   = new FsmState<EVENT>( AuthenticatedPlace.class.getSimpleName(),   EVENT.class, null, onExit, null );
      stateAuthorized      = new FsmState<EVENT>( AuthorizedPlace.class.getSimpleName(),      EVENT.class, null, onExit, null );
      stateD3              = new FsmState<EVENT>( D3Place.class.getSimpleName(),              EVENT.class, null, onExit, null );
      stateHelp            = new FsmState<EVENT>( HelpPlace.class.getSimpleName(),            EVENT.class, null, onExit, null );
      stateHome            = new FsmState<EVENT>( HomePlace.class.getSimpleName(),            EVENT.class, null, onExit, null );
      statePhoneGap        = new FsmState<EVENT>( PhoneGapPlace.class.getSimpleName(),        EVENT.class, null, onExit, null );
      stateSettings        = new FsmState<EVENT>( SettingsPlace.class.getSimpleName(),        EVENT.class, null, onExit, null );
      stateUnauthenticated = new FsmState<EVENT>( UnauthenticatedPlace.class.getSimpleName(), EVENT.class, null, null,   null );
      stateUnauthorized    = new FsmState<EVENT>( UnauthorizedPlace.class.getSimpleName(),    EVENT.class, null, null,   null );

      // guards
      GuardNav guardNav = new GuardNav( this );

      //GuardIsAuthenticated guardAuthenticated = new GuardIsAuthenticated( this );

      //GuardIsAuthorized guardAuthorized = new GuardIsAuthorized( this );

      // handlers
      OnGoBack onBack = new OnGoBack();

      //OnGoToInvariant onAuthenticated   = new OnGoToInvariant( AuthenticatedPlace.INSTANCE );
      //OnGoToInvariant onAuthorized      = new OnGoToInvariant( AuthorizedPlace.INSTANCE );
      OnGoToInvariant onD3                = new OnGoToInvariant( D3Place.INSTANCE );
      //OnGoToInvariant onHome            = new OnGoToInvariant( HomePlace.INSTANCE );
      OnGoToInvariant onPhoneGap            = new OnGoToInvariant( PhoneGapPlace.INSTANCE );
      //OnGoToInvariant onSettings        = new OnGoToInvariant( SettingsPlace.INSTANCE );
      //OnGoToInvariant onUnauthenticated = new OnGoToInvariant( UnauthenticatedPlace.INSTANCE );
      //OnGoToInvariant onUnauthorized    = new OnGoToInvariant( UnauthorizedPlace.INSTANCE );

      OnGoToVariant<HelpPlace> onHelp = new OnGoToVariant<HelpPlace>( new HelpPlace( "" ) ); // token set in handleEvent

      // from home
      stateHome.addTransition( statePhoneGap, EVENT.NAV_FORWARD, guardNav, onPhoneGap );
      stateHome.addTransition( stateHelp,     EVENT.HELP,        null,     onHelp );
      //stateHome.addTransition( stateSettings, EVENT.SETTINGS,    null,     onSettings );

      // from phonegap
      statePhoneGap.addTransition( stateHelp, EVENT.HELP,        null,     onHelp );
      statePhoneGap.addTransition( stateHome, EVENT.NAV_BACK,    guardNav, onBack );
      statePhoneGap.addTransition( stateD3,   EVENT.NAV_FORWARD, guardNav, onD3 );

      // from d3
      stateD3.addTransition( stateHelp,     EVENT.HELP,     null,     onHelp );
      stateD3.addTransition( statePhoneGap, EVENT.NAV_BACK, guardNav, onBack );

      // from authenticated
      //stateAuthenticated.addTransition( stateAuthorized,   EVENT.NAV_FORWARD,  guardAuthorized, onAuthorized );
      //stateAuthenticated.addTransition( stateUnauthorized, EVENT.UNAUTHORIZED, null,            onUnauthorized );
      //stateAuthenticated.addTransition( stateHome,         EVENT.NAV_BACK,     guardNav,        onBack );

      // from authorized
      //stateAuthorized.addTransition( stateAuthenticated, EVENT.NAV_BACK, guardNav, onBack );

      // from unauthenticated
      //stateUnauthenticated.addTransition( stateHome, EVENT.NAV_BACK, guardNav, onBack );

      // from unauthorized
      //stateUnauthorized.addTransition( stateAuthenticated, EVENT.NAV_BACK, guardNav, onBack );

      // from help
      stateHelp.addTransition( stateD3,       EVENT.NAV_BACK, guardNav, onBack );
      stateHelp.addTransition( stateHome,     EVENT.NAV_BACK, guardNav, onBack );
      stateHelp.addTransition( statePhoneGap, EVENT.NAV_BACK, guardNav, onBack );
      //stateHelp.addTransition( stateSettings, EVENT.NAV_BACK, guardNav, onBack );
      //stateHelp.addTransition( stateSettings, EVENT.SETTINGS, null,     onSettings );

      // from settings
      //stateSettings.addTransition( stateHome,  EVENT.NAV_BACK, guardNav, onBack );
      //stateSettings.addTransition( stateHelp,  EVENT.HELP,     null,     onHelp );
   }
}
