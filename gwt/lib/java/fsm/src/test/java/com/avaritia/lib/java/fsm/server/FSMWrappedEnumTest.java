package com.avaritia.lib.java.fsm.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import com.avaritia.lib.java.fsm.client.Event;
import com.avaritia.lib.java.fsm.shared.FSM;
import com.avaritia.lib.java.fsm.shared.FSM.EventHandler;
import com.avaritia.lib.java.fsm.shared.FSM.FsmEvent;
import com.avaritia.lib.java.fsm.shared.FSM.FsmState;
import com.avaritia.lib.java.fsm.shared.FSM.Guard;
import com.avaritia.lib.java.fsm.shared.FSM.Transition;


public class FSMWrappedEnumTest extends FSMTestBase {
   private enum Events {
      A, B, C, D, E, F;
   }


   public static class Wrapped implements Event<Events> {
      static private final Map<Events, Wrapped> enum2wrapped = new HashMap<Events, Wrapped>();

      static public Wrapped toWrapped( Events evt ) {
         return enum2wrapped.get( evt );
      }


      private Events type;

      private boolean handled;

      private Object payload;


      public Wrapped( Events type ) {
         this.type = type;

         enum2wrapped.put( type, this );
      }


      @Override public Object getPayload() {
         return payload;
      }


      @Override public Event<Events> setPayload( Object payload ) {
         this.payload = payload;

         return this;
      }


      @Override public boolean isHandled() {
         return handled;
      }


      @Override public Event<Events> setHandled( boolean handled ) {
         this.handled = handled;

         return this;
      }


      @Override public Events getType() {
         return type;
      }
   }


   static protected final Wrapped A = new Wrapped( Events.A );
   static protected final Wrapped B = new Wrapped( Events.B );
   static protected final Wrapped C = new Wrapped( Events.C );


   private static class TestGuardA implements Guard<Events> {
      @Override public boolean accept( Transition<Events> t ) {
         return t.getTrigger() == A.getType();
      }
   }


   private static class TestGuardNotHandled implements Guard<Events> {
      @Override public boolean accept( Transition<Events> t ) {
         //System.err.println( String.format( "%s handled == %s", t.getTrigger().name(), Wrapped.toWrapped( t.getTrigger() ).isHandled() ) );
         return !Wrapped.toWrapped( t.getTrigger() ).isHandled();
      }
   }


   private static class TestCallback implements EventHandler<Events> {
      @Override public void handleEvent( FsmEvent<Events> evt ) {
         assertTrue( evt instanceof Wrapped );
         assertEquals( A, evt );
         assertEquals( 69, A.getPayload() );
      }
   }


   private static class TestHandled implements EventHandler<Events> {
      @Override public void handleEvent( FsmEvent<Events> evt ) {
         Object o = evt;

         assertTrue( o instanceof Wrapped );

         Wrapped w = (Wrapped) o;

         w.setHandled( true );
      }
   }


   protected FsmState<Events> s1;
   protected FsmState<Events> s2;
   protected FsmState<Events> s3;


   @Before public void setUp() {
      s1 = new FsmState<Events>( "s1", Events.class );
      s2 = new FsmState<Events>( "s2", Events.class );
      s3 = new FsmState<Events>( "s3", Events.class );
   }


   @Test public void testGuard() {
      TestGuardA g = new TestGuardA();
      TestCallback cb = new TestCallback();

      s1.addTransition( s2, Events.A, g, cb );
      s1.addTransition( s2, Events.B, g, cb );

      FSM<Events> fsm = new FSM<Events>( s1 );

      fsm.deliver( B );

      assertEquals( s1, fsm.getCurrent() );

      fsm.deliver( A.setPayload( new Integer( 69 ) ) );

      assertEquals( s2, fsm.getCurrent() );
   }


   @Test public void testHandled() {
      TestGuardNotHandled g = new TestGuardNotHandled();
      TestHandled cb = new TestHandled();

      s1.addTransition( s2, Events.A, g, cb );
      s2.addTransition( s3, Events.A, g, cb );

      FSM<Events> fsm = new FSM<Events>( s1 );

      fsm.deliver( A );

      assertEquals( s2, fsm.getCurrent() );
   }


   @Test public void testTransition() {
      TestGuardA g = new TestGuardA();
      TestCallback cb = new TestCallback();

      s1.addTransition( s2, Events.A, g, cb );
      s1.addTransition( s2, Events.B );

      FSM<Events> fsm = new FSM<Events>( s1 );

      fsm.deliver( A.setPayload( new Integer( 69 ) ) );

      assertEquals( s2, fsm.getCurrent() );
   }
}
