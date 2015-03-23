package com.avaritia.lib.java.fsm.server;

import org.junit.BeforeClass;
import com.avaritia.lib.java.fsm.shared.FSM;
import com.avaritia.lib.java.fsm.shared.Logger;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;


abstract public class FSMTestBase {
   @BeforeClass static public void beforeClass() {
      Guice.createInjector( new AbstractModule() {
         @Override protected void configure() {
            bind( Logger.class ).to( com.avaritia.lib.java.fsm.server.LoggerImpl.class );

            requestStaticInjection( FSM.class );
         }
      } );
   }
}

