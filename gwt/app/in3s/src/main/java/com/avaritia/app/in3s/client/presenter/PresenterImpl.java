// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.avaritia.app.in3s.client.FiniteStateMachine;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;


abstract public class PresenterImpl implements Presenter {
   abstract public String getHelpToken();

   @Inject protected FiniteStateMachine fsm;

   protected EventBus eventBus;


   @Override public void onBack() {
      if ( eventBus != null ) fsm.deliver( FiniteStateMachine.NAV_BACK );
   }


   @Override public void onForward() {
      if ( eventBus != null ) fsm.deliver( FiniteStateMachine.NAV_FORWARD );
   }


   @Override public void onHelp() {
      if ( eventBus != null ) fsm.deliver( FiniteStateMachine.HELP.setPayload( getHelpToken() ) );
   }


   @Override public void onHome() {
      if ( eventBus != null ) fsm.deliver( FiniteStateMachine.HOME );
   }


   @Override public void onSettings() {
      if ( eventBus != null ) fsm.deliver( FiniteStateMachine.SETTINGS );
   }
}
