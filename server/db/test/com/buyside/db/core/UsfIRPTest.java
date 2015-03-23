// $Id: UsfIRPTest.java 1220 2013-10-30 17:49:31Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import static org.junit.Assert.assertTrue;
import javax.persistence.TypedQuery;
import org.junit.Test;

/**
 * @author dave
 * 
 */
public class UsfIRPTest extends EntityManagerBase {
   /**
    * Count the number of bonds in USF_IRP.
    */
   @Test public void testCount() {
      TypedQuery<Long> query = em.createQuery( "SELECT COUNT( b ) FROM UsfIRP b", Long.class );
      Long count = query.getSingleResult();

      assertTrue( count >= 6 ); // TU, 3Y, FV, TY, US, WN
   }

   
   /**
    * Try to insert an invalid DB_NAME.  TODO: figure out why this doesn't throw.
   @Test( expected = javax.validation.ConstraintViolationException.class )
   public void testDbNameRegex() {
      UsfIRP future = new UsfIRP(); 

      future.setDbName( "invalid" );
      
      EntityTransaction transaction =  em.getTransaction();
      
      transaction.begin();
      em.persist( future );
      transaction.commit();
   }
   */
}
