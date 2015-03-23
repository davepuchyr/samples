/**
 * 
 */
package com.buyside.automata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 * Sets the order of test execution of class {@link SchedulerTest} so that stateful members are set appropriately (for efficiency).
 * 
 * @author dave
 * 
 */
public class SchedulerRunner extends BlockJUnit4ClassRunner {
   public SchedulerRunner( Class<?> klass ) throws InitializationError {
      super( klass );
   }


   /**
    * Sets the test order.
    */
   @Override protected List<FrameworkMethod> computeTestMethods() {
      List<FrameworkMethod> list = super.computeTestMethods();
      FrameworkMethod[] copy = new FrameworkMethod[ list.size() ];
      Map<String, Integer> name2ordinal = new HashMap<String, Integer>();
      int i = 0;
      name2ordinal.put( "testAddFactory", i++ );
      name2ordinal.put( "testTreasuryDirectAnnouncementsJob", i++ );
      name2ordinal.put( "testTreasuryDirectAnnouncementsJobDecode", i++ );
      name2ordinal.put( "testTreasuryDirectAuctionsJob", i++ );
      name2ordinal.put( "testTreasuryDirectAuctionsJobDecode", i++ );
      
      for ( FrameworkMethod fm : list ) {
         String name = fm.getName();
         
         if ( name2ordinal.containsKey( name ) ) {
            copy[name2ordinal.get( name )] = fm;
         } else {
            copy[i++] = fm;
         }
      }

      return Arrays.asList( copy );
   }
}
