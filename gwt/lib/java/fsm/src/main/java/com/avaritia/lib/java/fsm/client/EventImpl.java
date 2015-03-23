// $Id$
package com.avaritia.lib.java.fsm.client;


public class EventImpl<E extends Enum<E>> implements Event<E> {
   E type;

   private boolean handled;

   private Object payload;


   public EventImpl( E type ) {
      this.type = type;
   }


   @Override public E getType() {
      return type;
   }


   @Override public Object getPayload() {
      return payload;
   }


   @Override public Event<E> setPayload( Object payload ) {
      this.payload = payload;

      return this;
   }


   @Override public boolean isHandled() {
      return handled;
   }


   @Override public Event<E> setHandled( boolean handled ) {
      this.handled = handled;

      return this;
   }


   @Override public String toString() {
      return type.name();
   }
}
