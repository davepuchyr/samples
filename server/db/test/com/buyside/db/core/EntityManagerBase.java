/**
 * 
 */
package com.buyside.db.core;

import java.io.IOException;
import javax.persistence.EntityManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author dave
 * 
 */
public abstract class EntityManagerBase {
   static protected EntityManager em = null;


   @BeforeClass static public void beforeClass() throws InstantiationException, IllegalAccessException, IOException {
      em = DerbyDatabase.getCoreEntityManager();
   }


   @AfterClass static public void afterClass() {
      em.close();
      em = null;
   }
}
