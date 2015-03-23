// $Id$
package com.avaritia.lib.java.fsm.client;

import com.avaritia.lib.java.fsm.shared.FSM.FsmEvent;


public interface Event<E extends Enum<E>> extends FsmEvent<E> {
   public boolean isHandled();

   public Event<E> setHandled( boolean handled );


   public Object getPayload();

   public Event<E> setPayload( Object payload );
}
