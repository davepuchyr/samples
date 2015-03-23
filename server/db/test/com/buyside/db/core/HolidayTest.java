// $Id: HolidayTest.java 1253 2013-12-05 19:12:32Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.TypedQuery;
import org.junit.Test;

/**
 * @author dave
 */
public class HolidayTest extends EntityManagerBase {
   SimpleDateFormat formatter = new SimpleDateFormat( "MM/dd/yyyy" );

   
   @Test public void test2013() throws ParseException {
      String[] dates = new String[] { "1/1/2013", "1/21/2013", "2/18/2013", "3/29/2013", "5/27/2013", "7/4/2013", "9/2/2013", "10/14/2013", "11/11/2013", "11/28/2013", "12/25/2013" };
      Date start = formatter.parse( "1/1/2013" );
      Date end = formatter.parse( "12/31/2013" );

      TypedQuery<Holiday> q = em.createQuery( "SELECT b FROM Holiday b WHERE :start <= b.date AND b.date <= :end ORDER BY b.date ASC", Holiday.class );
      
      q.setParameter( "start", start );
      q.setParameter( "end", end );
      
      List<Holiday> list = q.getResultList();

      assertTrue( dates.length == list.size() );

      for ( int i = 0, n = dates.length; i < n; ++i ) {
         assertEquals( dates[i], formatter.parse( dates[i] ), list.get( i ).getDate() );
      }
   }
   
   
   @Test public void testSandy() throws ParseException {
      Date sandy = formatter.parse( "10/30/2012" );
      TypedQuery<Holiday> q = em.createQuery( "SELECT b FROM Holiday b WHERE b.date = :sandy", Holiday.class );
      
      q.setParameter( "sandy", sandy );
      
      Holiday holiday = q.getSingleResult();

      assertEquals( formatter.format( sandy ), sandy, holiday.getDate() );
   }
}
