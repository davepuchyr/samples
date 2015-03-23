// $Id: UstBondTest.java 1220 2013-10-30 17:49:31Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import static org.junit.Assert.assertTrue;
import javax.persistence.TypedQuery;
import org.junit.Test;

/**
 * @author dave
 * 
 */
public class UstBondTest extends EntityManagerBase {
   /**
    * Count the number of bonds in UST_BONDS.
    */
   @Test public void testCount() {
      TypedQuery<Long> query = em.createQuery( "SELECT COUNT( b ) FROM UstBond b", Long.class );
      Long count = query.getSingleResult();

      assertTrue( count >= 647 ); // as of 2013.06.11 there are 647 bonds
   }

   
   /**
    * Read from UST_BONDS.
    */
   @Test public void testRead() {
      TypedQuery<UstBond> query = em.createQuery( "SELECT b FROM UstBond b WHERE b.cusip = :cusip", UstBond.class );
      query.setParameter( "cusip", "912828SW1" );
      UstBond b = query.getSingleResult();

      assertTrue( "UST_20140531_00.250".equals( b.getDbName() ) );
   }
}
