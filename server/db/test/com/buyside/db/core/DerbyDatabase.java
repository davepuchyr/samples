// $Id: DerbyDatabase.java 1074 2013-06-11 17:22:46Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CLASSLOADER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_DRIVER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_PASSWORD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_URL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.JDBC_USER;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_LEVEL;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_SESSION;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_THREAD;
import static org.eclipse.persistence.config.PersistenceUnitProperties.LOGGING_TIMESTAMP;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TARGET_DATABASE;
import static org.eclipse.persistence.config.PersistenceUnitProperties.TRANSACTION_TYPE;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.apache.commons.transaction.util.FileHelper;
import com.buyside.db.init.DDLGenerator;

/**
 * @author dave
 * 
 */
public class DerbyDatabase {
   static private DerbyDatabase database = null;
   static private Properties properties = null;
   
   
   static public EntityManagerFactory getCoreEntityManagerFactory() {
      return Persistence.createEntityManagerFactory( "domain", properties );
   }

   
   static public EntityManager getCoreEntityManager() throws InstantiationException, IllegalAccessException, IOException {
      if ( database == null ) database = new DerbyDatabase();

      return getCoreEntityManagerFactory().createEntityManager( properties );
   }
   
   
   private DerbyDatabase() throws InstantiationException, IllegalAccessException, IOException {
      String dirDerby = "derby";
      System.out.print( "Creating derby database in ./" + dirDerby + "..." );

      properties = new Properties();

      properties.put( TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name() );
      properties.put( CLASSLOADER, DerbyDatabase.class.getClassLoader() );

      properties.put( TARGET_DATABASE, "Derby" );
      properties.put( JDBC_DRIVER, "org.apache.derby.jdbc.EmbeddedDriver" );
      properties.put( JDBC_URL, "jdbc:derby:" + dirDerby + "/db;create=true" );
      properties.put( JDBC_USER, "" );
      properties.put( JDBC_PASSWORD, "" );
      properties.put( DDL_GENERATION, "create-tables" );

      if ( properties.get( TARGET_DATABASE ).toString().toLowerCase().equals( "derby" ) ) {
         File database = new File( dirDerby );
         if ( database.exists() ) FileHelper.removeRec( database ); // rm -rf
      }

      properties.put( LOGGING_SESSION, "true" );
      properties.put( LOGGING_THREAD, "true" );
      properties.put( LOGGING_TIMESTAMP, "true" );
      properties.put( LOGGING_LEVEL, "SEVERE" );
      //properties.put( LOGGING_LEVEL, "FINEST" );
      //properties.put( "eclipselink.logging.level.sql", "FINE" );
      //properties.put( "eclipselink.logging.parameters", "true" );

      EntityManagerFactory emf = getCoreEntityManagerFactory();
      EntityManager em = emf.createEntityManager( properties );
      DDLGenerator generator = new com.buyside.db.init.DDLGenerator( em );
      
      // read-only db
      Properties production = new Properties();
      String etc =  System.getProperty( "user.dir" );
      etc = etc.indexOf( "db" ) == -1 ? etc += "/etc" : etc.replace( "db", "etc" ); // cope with build in both db and Applications directory
      File file = new File( etc + "/db.production-read-only.properties" );
      
      if ( file.exists() ) {
         System.out.print( "loading " + file + "..." );
         
         production.load( new FileInputStream( file ) );
      } else {
         System.err.println( "Failed to read the read-only production properties file." );
         System.exit( -1 );
      }

      DDLGenerator.setJpaProperties( production );
      EntityManagerFactory emfProduction = Persistence.createEntityManagerFactory( production.get( "com.buyside.db.init.core.pu" ).toString(), production );
      EntityManager emProduction = emfProduction.createEntityManager( production );

      generator.populate( em, emProduction );

      em.close();
      emf.close();
      emProduction.close();
      emfProduction.close();
      
      System.out.println( "done." );
   }
}
