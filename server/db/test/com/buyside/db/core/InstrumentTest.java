// $Id: InstrumentTest.java 1220 2013-10-30 17:49:31Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javax.persistence.TypedQuery;
import org.junit.Test;

/**
 * @author dave
 * 
 */
public class InstrumentTest extends EntityManagerBase {
   /**
    * Read a UstBond from INSTRUMENTS.
    */
   @Test public void testReadUstBond() {
      String cusip = "912810CT3";
      TypedQuery<Instrument> query = em.createQuery( "SELECT b FROM Instrument b WHERE b.name LIKE :cusip", Instrument.class );
      query.setParameter( "cusip", "%CT3" );
      Instrument i = query.getSingleResult();
      UstBond bond = i.getBond();
      
      assertNotNull( bond );
      assertNull( i.getBill() );
      assertNull( i.getFuture() );
      assertTrue( cusip.equals( i.getName() ) );
      assertTrue( cusip.equals( i.getBond().getCusip() ) );
      
      query = em.createQuery( "SELECT b FROM Instrument b WHERE b.name LIKE :dbname", Instrument.class );
      query.setParameter( "dbname", bond.getDbName() );
      i = query.getSingleResult();
      bond = i.getBond();

      assertNotNull( bond );
      assertNull( i.getBill() );
      assertNull( i.getFuture() );
      assertTrue( cusip.equals( bond.getCusip() ) );
   }
}
