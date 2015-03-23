// $Id: DDLGenerator.java 1296 2014-02-21 22:01:36Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.init;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;
import org.reflections.Reflections;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import com.buyside.common.BuySideEntity;
import com.buyside.db.EntityHelperService;
import com.buyside.db.EntityHelperService.EntityDefinition;
import com.buyside.db.EntityHelperServiceImpl;
import com.buyside.db.core.Account;
import com.buyside.db.core.AuctionDay;
import com.buyside.db.core.Close;
import com.buyside.db.core.CloseType;
import com.buyside.db.core.Counterparty;
import com.buyside.db.core.Currency;
import com.buyside.db.core.Firm;
import com.buyside.db.core.Holiday;
import com.buyside.db.core.Instrument;
import com.buyside.db.core.Role;
import com.buyside.db.core.RoleType;
import com.buyside.db.core.User;
import com.buyside.db.core.UsfIRP;
import com.buyside.db.core.UsfMacro;
import com.buyside.db.core.UstBill;
import com.buyside.db.core.UstBond;
import com.buyside.db.core.Vector;
import com.buyside.db.pnl.Action;
import com.buyside.db.pnl.ExecType;
import com.buyside.db.pnl.Market;
import com.buyside.db.pnl.Position;
import com.buyside.db.pnl.ReportType;
import com.buyside.db.pnl.TradeType;

/**
 * @author dave
 */
public class DDLGenerator {
   static private String modifier = DDLGenerator.class.getSimpleName();
   
   
   public DDLGenerator( EntityManager em ) {
      Map<String, Object> properties = em.getProperties();
      
      // HACK: create _HISTORY tables
      EntityHelperService ehs = new EntityHelperServiceImpl();
      Reflections reflections = new Reflections( "com.buyside.db" );
      Set<Class<?>> annotated = reflections.getTypesAnnotatedWith( Entity.class );

      if ( properties.get( TARGET_DATABASE ).toString().equalsIgnoreCase( "mysql" ) ) {
         for ( Class<?> clazz : annotated ) {
            System.out.println( "clazz == " + clazz.getName() );
            EntityDefinition ed = ehs.getEntityDefinition( clazz );
            System.out.println( String.format( "table == %s; columns = %s", ed.getTable(), ed.getColumns() ) );
            ClassDescriptor classDescriptor = em.unwrap( Session.class ).getDescriptor( clazz ); 
            //System.out.println( "classDescriptor.getDefaultTable() == " + classDescriptor.getDefaultTable() );
            String schema = classDescriptor.getDefaultTable().getTableQualifier();
            String table = String.format( "%s.%s", schema, ed.getTable() );
            String drop = "DROP TABLE IF EXISTS " + table + "_HISTORY; "; // HARD-CODED
   
            em.getTransaction().begin();
            em.createNativeQuery( drop ).executeUpdate();
            em.getTransaction().commit();
   
            String create = "CREATE TABLE " + table + "_HISTORY LIKE " + table + "; "; // HARD-CODED
   
            em.getTransaction().begin();
            em.createNativeQuery( create ).executeUpdate();
            em.getTransaction().commit();
   
            String start = "ALTER TABLE " + table + "_HISTORY ADD START DATETIME NULL DEFAULT NULL; ";  // HARD-CODED
            
            em.getTransaction().begin();
            em.createNativeQuery( start ).executeUpdate();
            em.getTransaction().commit();
   
            String finish = "ALTER TABLE " + table + "_HISTORY ADD FINISH DATETIME NULL DEFAULT NULL; "; // HARD-CODED
            
            em.getTransaction().begin();
            em.createNativeQuery( finish ).executeUpdate();
            em.getTransaction().commit();
            
            String droppk = "ALTER TABLE " + table + "_HISTORY DROP PRIMARY KEY; "; // HARD-CODED
            
            em.getTransaction().begin();
            em.createNativeQuery( droppk ).executeUpdate();
            em.getTransaction().commit();
            
            if ( table.equalsIgnoreCase( "CORE.INSTRUMENTS" ) ||
                 table.equalsIgnoreCase( "CORE.VECTORS" ) ||
                 table.equalsIgnoreCase( "CORE.COUNTERPARTIES" ) ||
                 table.equalsIgnoreCase( "CORE.FIRMS" ) ||
                 table.equalsIgnoreCase( "CORE.ACCOUNTS" ) ) {
                String dropindex = "ALTER TABLE " + table + "_HISTORY DROP INDEX `NAME`; "; // HARD-CODED
                
                em.getTransaction().begin();
                em.createNativeQuery( dropindex ).executeUpdate();
                em.getTransaction().commit();
            }
             
            if ( table.equalsIgnoreCase( "CORE.CURRENCIES" ) ) {
               String dropindex = "ALTER TABLE " + table + "_HISTORY DROP INDEX `CODE`; "; // HARD-CODED
               
               em.getTransaction().begin();
               em.createNativeQuery( dropindex ).executeUpdate();
               em.getTransaction().commit();
            }
            
            if ( table.equalsIgnoreCase( "CORE.HOLIDAYS" ) ) {
               String dropindex = "ALTER TABLE " + table + "_HISTORY DROP INDEX `DATE`; "; // HARD-CODED
               
               em.getTransaction().begin();
               em.createNativeQuery( dropindex ).executeUpdate();
               em.getTransaction().commit();
            }
            
            if ( table.equalsIgnoreCase( "CORE.USF_IRPS" ) ||
                 table.equalsIgnoreCase( "CORE.USF_MACROS" ) ||
                 table.equalsIgnoreCase( "CORE.UST_BONDS" ) ) {
               String dropindex = "ALTER TABLE " + table + "_HISTORY DROP INDEX `DB_NAME`; "; // HARD-CODED
               
               em.getTransaction().begin();
               em.createNativeQuery( dropindex ).executeUpdate();
               em.getTransaction().commit();
            }
            
            if ( table.equalsIgnoreCase( "CORE.FIRMS" ) ||
                 table.equalsIgnoreCase( "CORE.COUNTERPARTIES" ) ) { // HACK: handle BTEC and BTec; I couldn't get columnDefinition to work 
               String casesensitive = "ALTER TABLE " + table + " CHANGE  `NAME`  `NAME` VARCHAR( 255 ) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL";
               
               em.getTransaction().begin();
               em.createNativeQuery( casesensitive ).executeUpdate();
               em.getTransaction().commit();
            }            
         }
      } else {
         for ( Class<?> clazz : annotated ) {
            EntityDefinition ed = ehs.getEntityDefinition( clazz );
            String schema = em.unwrap( Session.class ).getDescriptor( clazz ).getDefaultTable().getTableQualifier();
            String table = String.format(  "%s.%s", schema, ed.getTable() );
            String create = "CREATE TABLE " + table + "_HISTORY ( "; // HARD-CODED
            
            try {
               em.getTransaction().begin();
               
               Connection connection = em.unwrap( Connection.class );
               String query = "SELECT * FROM " + table; // HARD-CODED
               PreparedStatement pstmt = connection.prepareStatement( query );
               ResultSetMetaData rsmd = pstmt.getMetaData();
               int i = 0, numColumns = rsmd.getColumnCount();
               
               while ( ++i <= numColumns ) {
                  String type = rsmd.getColumnTypeName( i );
                  if ( type.equals( "VARCHAR" ) ) type += "(255)"; // HARD-CODED
                  create += rsmd.getColumnName( i ) + " " + type + ", "; // HARD-CODED
               }
               
               create = create + "START TIMESTAMP DEFAULT NULL, FINISH TIMESTAMP DEFAULT NULL )"; // HARD-CODED

               em.getTransaction().commit();
            } catch ( SQLException e ) {
               e.printStackTrace();
            }

            em.getTransaction().begin();
            em.createNativeQuery( create ).executeUpdate();
            em.getTransaction().commit();
         }
      }
   }
   
   
   public void populate( EntityManager em, EntityManager emProduction ) throws IOException, InstantiationException, IllegalAccessException {
      Optional i = new Optional( new ParseInt() );
      //Optional l = new Optional( new ParseLong() );
      Optional f = new Optional( new ParseDouble() );
      Optional d = new Optional( new ParseDate( "MM/dd/yyyy" ) );

      if ( emProduction == null ) {
         populateFromCSV( em, "/com/buyside/db/init/USF_IRPS.csv", UsfIRP.class, new CellProcessor[] { i, null, null } );
         populateFromCSV( em, "/com/buyside/db/init/USF_MACROS.csv", UsfMacro.class, new CellProcessor[] { i, null, null } );
         populateFromCSV( em, "/com/buyside/db/init/UST_BILLS.csv", UstBill.class, new CellProcessor[] { i, null } );
         populateFromCSV( em, "/com/buyside/db/init/CASH_STATIC.csv", UstBond.class, new CellProcessor[] {
            null, // DB_NAME
            null, // CUSIP
            null, // TICKER
            f, // COUPON
            d, // MATURITY
            d, // INTEREST_ACCURAL_DATE: legacy, equals DATED_DATE
            d, // ISSUE_DATE
            d, // AUCTION_DATE
            d, // ANNOUNCEMENT_DATE
            d, // DATED_DATE
            d, // FIRST_TRADING_DATE
            d, // FIRST_COUPON_DATE
            d, // PENULTIMATE_COUPON_DATE
            f, // ISSUE_SIZE
            f, // AUCTION_YIELD
            f, // AUCTION_TAIL
            f, // BID_TO_COVER_RATIO
            i, // TENOR
            f, // INDIRECT_BID_PERCENTAGE
            f, // DIRECT_BID_PERCENTAGE
            f, // STREET_PERCENTAGE
            i, // NUMBER_OF_SCHEDULED_REOPENINGS
            i, // NUMBER_OF_ACCIDENTAL_REOPENINGS
            d, // SCHEDULED_REOPEN_AUCTION_DATE_1
            d, // SCHEDULED_REOPEN_AUCTION_DATE_2
            d, // SCHEDULED_REOPEN_AUCTION_DATE_3
            d, // ACCIDENTAL_REOPEN_AUCTION_DATE_1
            d, // ACCIDENTAL_REOPEN_AUCTION_DATE_2
            f, // SCHEDULED_REOPEN_ISSUE_SIZE_1
            f, // SCHEDULED_REOPEN_ISSUE_SIZE_2
            f, // SCHEDULED_REOPEN_ISSUE_SIZE_3
            f, // ACCIDENTAL_REOPEN_ISSUE_SIZE_1
            f, // ACCIDENTAL_REOPEN_ISSUE_SIZE_2
            f, // BUYBACK_SIZE
            f, // SCHEDULED_REOPEN_AUCTION_YIELD_1
            f, // SCHEDULED_REOPEN_AUCTION_YIELD_2
            f, // SCHEDULED_REOPEN_AUCTION_YIELD_3
            f, // ACCIDENTAL_REOPEN_AUCTION_YIELD_1
            f, // ACCIDENTAL_REOPEN_AUCTION_YIELD_2
            f, // SCHEDULED_REOPEN_AUCTION_TAIL_1
            f, // SCHEDULED_REOPEN_AUCTION_TAIL_2
            f, // SCHEDULED_REOPEN_AUCTION_TAIL_3
            f, // ACCIDENTAL_REOPEN_AUCTION_TAIL_1
            f, // ACCIDENTAL_REOPEN_AUCTION_TAIL_2
            f, // SCHEDULED_REOPEN_BID_TO_COVER_RATIO_1
            f, // SCHEDULED_REOPEN_BID_TO_COVER_RATIO_2
            f, // SCHEDULED_REOPEN_BID_TO_COVER_RATIO_3
            f, // ACCIDENTAL_REOPEN_BID_TO_COVER_RATIO_1
            f, // ACCIDENTAL_REOPEN_BID_TO_COVER_RATIO_2
            f, // SCHEDULED_REOPEN_INDIRECT_BID_PERCENTAGE_1
            f, // SCHEDULED_REOPEN_INDIRECT_BID_PERCENTAGE_2
            f, // SCHEDULED_REOPEN_INDIRECT_BID_PERCENTAGE_3
            f, // ACCIDENTAL_REOPEN_INDIRECT_BID_PERCENTAGE_1
            f, // ACCIDENTAL_REOPEN_INDIRECT_BID_PERCENTAGE_2
            f, // SCHEDULED_REOPEN_DIRECT_BID_PERCENTAGE_1
            f, // SCHEDULED_REOPEN_DIRECT_BID_PERCENTAGE_2
            f, // SCHEDULED_REOPEN_DIRECT_BID_PERCENTAGE_3
            f, // ACCIDENTAL_REOPEN_DIRECT_BID_PERCENTAGE_1
            f, // ACCIDENTAL_REOPEN_DIRECT_BID_PERCENTAGE_2
            f, // SCHEDULED_REOPEN_STREET_PERCENTAGE_1
            f, // SCHEDULED_REOPEN_STREET_PERCENTAGE_2
            f, // SCHEDULED_REOPEN_STREET_PERCENTAGE_3
            f, // ACCIDENTAL_REOPEN_STREET_PERCENTAGE_1
            f, // ACCIDENTAL_REOPEN_STREET_PERCENTAGE_2
            f, // CURRENT_SIZE
            f, // FED_HOLDING
            i, // COUPON_FREQUENCY
            null, // COUPON_TYPE
            null, // DAY_COUNT
            null, // MINIMUM_PRICE_INCREMENT
            null, // SWAP_BOX_MINIMUM_PRICE_INCREMENT
            null, // PRICE_DISPLAY_CONVENTION
            null, // HOLIDAY_CALENDAR
            null, // SETTLEMENT_CONVENTION
            null, // SYMBOL_PREFIX
            i, // DVP
            null, // CALCULATION_TYPE
            null, // MINIMUM_TRADE_LOT_SIZE
            null, // TYPE
            null // RISK_POINT_MAPPING
         } );
      } else {
         TypedQuery<UstBond> qUstBond = emProduction.createQuery( "SELECT x FROM UstBond x", UstBond.class );
         List<UstBond> bonds = qUstBond.getResultList();
         
         em.getTransaction().begin();
         for ( UstBond x : bonds ) {
            x.setId( x.getId() );
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();

         //populateFromCSV( em, "/com/buyside/db/init/UST_BILLS.csv", UstBill.class, new CellProcessor[] { i, null } );
         TypedQuery<UstBill> qUstBill = emProduction.createQuery( "SELECT x FROM UstBill x", UstBill.class );
         List<UstBill> bills = qUstBill.getResultList();
         
         em.getTransaction().begin();
         for ( UstBill x : bills ) {
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();

         //populateFromCSV( em, "/com/buyside/db/init/USF_IRPS.csv", UsfIRP.class, new CellProcessor[] { null, d, d, d, d, d, null, null, null, i, f, f, null, null, null, null } );
         //populateFromCSV( em, "/com/buyside/db/init/USF_IRPS_BDPK.csv", UsfIRP.class, new CellProcessor[] { null, null, null, null, null, null, null, i, f, f, null, null, f } );
         //populateFromCSV( em, "/com/buyside/db/init/USF_IRPS_ED.csv", UsfIRP.class, new CellProcessor[] { null, null, null, d, d, d, null, i, f, f, null, null, f, null, null } );
         //populateFromCSV( em, "/com/buyside/db/init/USF_IRPS_FF.csv", UsfIRP.class, new CellProcessor[] { null, null, null, d, d, d, null, i, f, f, null, null, f, null, null } );
         TypedQuery<UsfIRP> qUsfIRP = emProduction.createQuery( "SELECT x FROM UsfIRP x", UsfIRP.class );
         List<UsfIRP> futures = qUsfIRP.getResultList();
         
         em.getTransaction().begin();
         for ( UsfIRP x : futures ) {
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();

         //populateFromCSV( em, "/com/buyside/db/init/USF_MACROS.csv", UsfMacro.class, new CellProcessor[] { i, null, null } );
         TypedQuery<UsfMacro> qUsfMacro = emProduction.createQuery( "SELECT x FROM UsfMacro x", UsfMacro.class );
         List<UsfMacro> macros = qUsfMacro.getResultList();
         
         em.getTransaction().begin();
         for ( UsfMacro x : macros ) {
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();
         
         //populateFromCSV( em, "/com/buyside/db/init/HOLIDAYS.csv", Holiday.class, new CellProcessor[] { i, d } );
         TypedQuery<Holiday> qHoliday = emProduction.createQuery( "SELECT x FROM Holiday x", Holiday.class );
         List<Holiday> holidays = qHoliday.getResultList();
         
         em.getTransaction().begin();
         for ( Holiday x : holidays ) {
            em.persist( x );
         }
         em.getTransaction().commit();
         
         //populateFromCSV( em, "/com/buyside/db/init/AUCTION_DAYS.csv", AuctionDay.class, new CellProcessor[] { i, i, d } );
         TypedQuery<AuctionDay> qAuctionDay = emProduction.createQuery( "SELECT x FROM AuctionDay x", AuctionDay.class );
         List<AuctionDay> auctionDays = qAuctionDay.getResultList();
         
         em.getTransaction().begin();
         for ( AuctionDay x : auctionDays ) {
            em.persist( x );
         }
         em.getTransaction().commit();
      }

      populateFromCSV( em, "/com/buyside/db/init/CURRENCIES.csv", Currency.class, new CellProcessor[] { i, null, null, null } );
      populateFromCSV( em, "/com/buyside/db/init/ACTIONS.csv", Action.class, new CellProcessor[] { i, null } );
      populateFromCSV( em, "/com/buyside/db/init/EXEC_TYPES.csv", ExecType.class, new CellProcessor[] { null } );
      populateFromCSV( em, "/com/buyside/db/init/FIRMS.csv", Firm.class, new CellProcessor[] { i, null } );
      populateFromCSV( em, "/com/buyside/db/init/MARKETS.csv", Market.class, new CellProcessor[] { null } );
      populateFromCSV( em, "/com/buyside/db/init/REPORT_TYPES.csv", ReportType.class, new CellProcessor[] { null } );
      populateFromCSV( em, "/com/buyside/db/init/CLOSE_TYPES.csv", CloseType.class, new CellProcessor[] { null } );
      populateFromCSV( em, "/com/buyside/db/init/ROLE_TYPES.csv", RoleType.class, new CellProcessor[] { null } );
      populateFromCSV( em, "/com/buyside/db/init/TRADE_TYPES.csv", TradeType.class, new CellProcessor[] { null } );
      populateFromCSV( em, "/com/buyside/db/init/USERS.csv", User.class, new CellProcessor[] { null, null, null } );
      populateFromCSV( em, "/com/buyside/db/init/VECTORS.csv", Vector.class, new CellProcessor[] { i, null } );

      populateAccounts( em, "/com/buyside/db/init/ACCOUNTS.csv" ); // has fk in FIRMS and ACCOUNT_TYPES
      populateRoles( em, "/com/buyside/db/init/ROLES.csv" ); // has fk in USERS
      populateInstruments( em ); // has fks in CURRENCIES, UST_BILLS, UST_BONDS, etc
      populateCounterparties( em, emProduction ); // has fks in ACCOUNTS, FIRMS, etc
      populateCloses( em, emProduction, "/com/buyside/db/init/CLOSES.csv" );
      populatePositions( em, emProduction, "/com/buyside/db/init/POSITIONS.csv" );
   }


   protected void populateFromCSV( EntityManager em, String file, Class<?> T, CellProcessor[] processors ) throws IOException, InstantiationException, IllegalAccessException {
      InputStream is = this.getClass().getResourceAsStream( file );
      InputStreamReader isr = new InputStreamReader( is );
      ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );

      try {
         Object o;
         final String[] header = reader.getHeader( true );
         
         if ( file.contains( "CASH_STATIC" ) ) header[5] = null; // HACK: skip legacy INTEREST_ACCURAL_DATE

         em.getTransaction().begin();

         while ( ( o = reader.read( T, header, processors ) ) != null ) {
            if ( o instanceof BuySideEntity ) {
               BuySideEntity entity = (BuySideEntity) o;
               
               entity.setModifier( modifier );

               em.persist( entity );
            } else {            
               em.persist( o );
            }
         }

         em.getTransaction().commit();
      } finally {
         reader.close();
         if ( em.getTransaction().isActive() ) {
            em.getTransaction().rollback();
         }
      }
   }

   
   protected void populateAccounts( EntityManager em, String file ) throws IOException, InstantiationException, IllegalAccessException {
      InputStream is = this.getClass().getResourceAsStream( file );
      InputStreamReader isr = new InputStreamReader( is );
      ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );
      Optional i = new Optional( new ParseInt() );
      CellProcessor[] processors = new CellProcessor[] { i, null, null };
      String jpql = String.format( "SELECT x FROM Firm x WHERE x.name = :name" );
      TypedQuery<Firm> q = em.createQuery( jpql, Firm.class );

      try {
         Object o;
         final String[] header = reader.getHeader( true );

         em.getTransaction().begin();
         
         while ( ( o = reader.read( AccountCSV.class, header, processors ) ) != null ) {
            AccountCSV row = (AccountCSV) o;
            
            q.setParameter( "name", row.getFirm() );

            Firm firm = q.getSingleResult();
            Account r = new Account();

            r.setId( row.getId() );
            r.setFirm( firm );
            r.setName( row.getName() );
            r.setModifier( modifier );

            em.persist( r );
         }

         em.getTransaction().commit();
      } catch ( Exception e ) {
         e.printStackTrace();
         throw new RuntimeException( e );
      } finally {
         reader.close();
         if ( em.getTransaction().isActive() ) {
            em.getTransaction().rollback();
         }
      }
   }
   
   
   protected void populateRoles( EntityManager em, String file ) throws IOException, InstantiationException, IllegalAccessException {
      InputStream is = this.getClass().getResourceAsStream( file );
      InputStreamReader isr = new InputStreamReader( is );
      ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );
      CellProcessor[] processors = new CellProcessor[] { null, null };

      try {
         Object o;
         final String[] header = reader.getHeader( true );

         em.getTransaction().begin();

         while ( ( o = reader.read( RoleCSV.class, header, processors ) ) != null ) {
            RoleCSV row = (RoleCSV) o;
            User user = em.find( User.class, row.getName() );
            RoleType roleType = em.find( RoleType.class, row.getRole() );
            Role r = new Role();

            r.setUser( user );
            r.setRoleType( roleType );
            r.setModifier( modifier );

            em.persist( r );
         }

         em.getTransaction().commit();
      } catch ( Exception e ) {
         e.printStackTrace();
         throw new RuntimeException( e );
      } finally {
         reader.close();
         if ( em.getTransaction().isActive() ) {
            em.getTransaction().rollback();
         }
      }
   }
   
   
   protected void populateInstruments( EntityManager em ) throws IOException, InstantiationException, IllegalAccessException {
      TypedQuery<Currency> qCurrency = em.createQuery( "SELECT x FROM Currency x", Currency.class );
      List<Currency> currencies = qCurrency.getResultList();
      
      em.getTransaction().begin();
      for ( Currency x : currencies ) {
         Instrument i = new Instrument();
            
         i.setName( x.getCode() );
         i.setCurrency( x );
         i.setModifier( modifier );
         
         em.persist( i );
      }
      em.getTransaction().commit();

      TypedQuery<UstBond> qBond = em.createQuery( "SELECT x FROM UstBond x", UstBond.class );
      List<UstBond> bonds = qBond.getResultList();
      
      em.getTransaction().begin();
      for ( UstBond x : bonds ) {
         Instrument i = new Instrument();
            
         i.setBond( x );
         i.setName( x.getDbName() );
         i.setModifier( modifier );
         
         em.persist( i );
         
         i = new Instrument();
         
         i.setBond( x );
         i.setName( x.getCusip() );
         i.setModifier( modifier );
         
         em.persist( i );
      }
      em.getTransaction().commit();

      TypedQuery<UstBill> qBill = em.createQuery( "SELECT x FROM UstBill x", UstBill.class );
      List<UstBill> bills = qBill.getResultList();
      
      em.getTransaction().begin();
      for ( UstBill x : bills ) {
         Instrument i = new Instrument();
         
         i.setBill( x );
         i.setName( x.getCusip() );
         i.setModifier( modifier );
         
         em.persist( i );
      }
      em.getTransaction().commit();

      TypedQuery<UsfIRP> qFuture = em.createQuery( "SELECT x FROM UsfIRP x WHERE x.lastTradeDate IS NULL OR ( x.lastTradeDate > :old AND x.lastTradeDate < :unborn )", UsfIRP.class );
      qFuture.setParameter( "old",    new Date( 1356998400L * 1000 ), TemporalType.DATE ); // 1356998400 == 2013.01.01; avoid duplicate names
      qFuture.setParameter( "unborn", new Date( 1672444800L * 1000 ), TemporalType.DATE ); // 1672444800 == 2022.12.31; avoid duplicate names

      List<UsfIRP> futures = qFuture.getResultList();
      
      em.getTransaction().begin();
      for ( UsfIRP x : futures ) {
         Instrument i = new Instrument();
         
         i.setFuture( x );
         i.setName( x.getDbName() );
         i.setModifier( modifier );
         
         em.persist( i );
         
         i = new Instrument();
         
         i.setFuture( x );
         i.setName( x.getTicker() );
         i.setModifier( modifier );
         
         em.persist( i );
      }
      em.getTransaction().commit();

      TypedQuery<UsfMacro> qMacro = em.createQuery( "SELECT x FROM UsfMacro x", UsfMacro.class );
      List<UsfMacro> macros = qMacro.getResultList();
      
      em.getTransaction().begin();
      for ( UsfMacro x : macros ) {
         Instrument i = new Instrument();
         
         i.setMacro( x );
         i.setName( x.getDbName() );
         i.setModifier( modifier );
         
         em.persist( i );
         
         i = new Instrument();
         
         i.setMacro( x );
         i.setName( x.getTicker() );
         i.setModifier( modifier );
         
         em.persist( i );
      }
      em.getTransaction().commit();
   }
   
   
   protected void populateCounterparties( EntityManager em, EntityManager emProduction ) {
      try {
         TypedQuery<Counterparty> q = emProduction.createQuery( "SELECT x FROM Counterparty x", Counterparty.class );
         List<Counterparty> counterparties = q.getResultList();
         
         em.getTransaction().begin();
         for ( Counterparty x : counterparties ) {
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();
      } catch ( Exception eProduction ) {
         Map<String, String> account2english = new HashMap<String, String>();
         account2english.put( "10200006", "DB Cash" ); // HARD-CODED
         account2english.put( "A16262", "DB Futures" );
         account2english.put( "Repo Margin (MNA statement)", "DB Repo" );
         account2english.put( "U1031181", "IB" );
         account2english.put( "AG:000049-05385", "NE Cash" );
         account2english.put( "Q 150-16220", "NE Futures" );
         account2english.put( "Cash", "9Alpha Cash" );
         account2english.put( "Futures", "9Alpha Futures" );      
         
         Map<String, String> firm2alias = new HashMap<String, String>();
         firm2alias.put( "DB", "Deutsche Bank" ); // HARD-CODED
         firm2alias.put( "DB", "Duetsche Bank" ); // MIT spelling mistake
         firm2alias.put( "New Edge", "Newedge" ); // MIT spelling mistake
         
         try {
            TypedQuery<Account> qAccount = em.createQuery( "SELECT x FROM Account x", Account.class );
            List<Account> accounts = qAccount.getResultList();
   
            em.getTransaction().begin();
            for ( Account x : accounts ) {
               Counterparty c = new Counterparty( x, null, null );
               String name = x.getName();
               
               c.setModifier( modifier );
               c.setName( name );
               em.persist( c );
               
               if ( account2english.containsKey( name ) ) {
                  //System.out.println( "populateCounterparties: name == " + name );
                  Account a = em.createQuery( "SELECT x FROM Account x WHERE x.name = :name", Account.class ).setParameter( "name", name ).getSingleResult();
                  Counterparty english = new Counterparty( a, null, null );
   
                  english.setModifier( modifier );
                  english.setName( account2english.get( name ) );
                  em.persist( english );
               }           
            }
            em.getTransaction().commit();
   
            TypedQuery<Firm> qFirm = em.createQuery( "SELECT x FROM Firm x", Firm.class );
            List<Firm> firms = qFirm.getResultList();
   
            em.getTransaction().begin();
            for ( Firm x : firms ) {
               Counterparty c = new Counterparty( null, em.merge( x ), null ); // old NOTE: use managed entity to avoid duping book and firm
               String name = x.getName();
               
               c.setModifier( modifier );
               c.setName( name );
               em.persist( c );
               
               if ( firm2alias.containsKey( name ) ) {
                  //System.out.println( "populateCounterparties: name == " + name );
                  Firm a = em.createQuery( "SELECT x FROM Firm x WHERE x.name = :name", Firm.class ).setParameter( "name", name ).getSingleResult();
                  Counterparty english = new Counterparty( null, a, null );
   
                  english.setModifier( modifier );
                  english.setName( firm2alias.get( name ) );
                  em.persist( english );
               }
            }
            em.getTransaction().commit();
   
            TypedQuery<Vector> qVector = em.createQuery( "SELECT x FROM Vector x", Vector.class );
            List<Vector> vectors = qVector.getResultList();
   
            em.getTransaction().begin();
            for ( Vector x : vectors ) {
               Counterparty c = new Counterparty( null, null, x );
               
               c.setModifier( modifier );
               c.setName( x.getName() );
               em.persist( c );
            }
            em.getTransaction().commit();
         } finally {
            if ( em.getTransaction().isActive() ) {
               em.getTransaction().rollback();
            }
         }
      }
   }
   
   
   protected void populateCloses( EntityManager em, EntityManager emProduction, String file ) throws IOException, InstantiationException, IllegalAccessException {
      try {
         TypedQuery<Close> qCloses = emProduction.createQuery( "SELECT x FROM Close x", Close.class );
         List<Close> closes = qCloses.getResultList();
         
         em.getTransaction().begin();
         for ( Close x : closes ) {
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();
      } catch ( Exception eProduction ) {
         System.out.println( "  * Failed to read the production CLOSES. *" );
         
         InputStream is = this.getClass().getResourceAsStream( file );
         InputStreamReader isr = new InputStreamReader( is );
         ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );
         Optional d = new Optional( new ParseDate( "MM/dd/yyyy" ) );
         Optional f = new Optional( new ParseDouble() );
         CellProcessor[] processors = new CellProcessor[] { d, null, null, null, f };
         String jpqlInstrument = String.format( "SELECT x FROM Instrument x WHERE x.name = :name" );
         TypedQuery<Instrument> qInstrument = em.createQuery( jpqlInstrument, Instrument.class );
         Currency currency = em.find( Currency.class, 1 ); // HARD-CODED
         CloseType typePrice = em.find( CloseType.class, "price" ); // HARD-CODED
         CloseType typeYield = em.find( CloseType.class, "yield" ); // HARD-CODED
         String jpqlCounterparty = String.format( "SELECT c FROM Counterparty c WHERE c.name = :name" );
         TypedQuery<Counterparty> qCounterparty = em.createQuery( jpqlCounterparty, Counterparty.class );
   
         try {
            Object o;
            final String[] header = reader.getHeader( true );
   
            em.getTransaction().begin();
            
            while ( ( o = reader.read( CloseCSV.class, header, processors ) ) != null ) {
               CloseCSV row = (CloseCSV) o;
               
               qCounterparty.setParameter( "name", row.getSource() );
               qInstrument.setParameter( "name", row.getInstrument() );
               
               //System.out.println( String.format(  "source == %s; instrument == %s", row.getSource(), row.getInstrument() ) );
   
               Counterparty counterparty = qCounterparty.getSingleResult();
               Instrument instrument = qInstrument.getSingleResult();
               Close r = new Close();
   
               r.setSource( counterparty );
               r.setInstrument( instrument );
               r.setDate( row.getDate() );
               r.setCurrency( currency );
               r.setType( row.getType().equals( "price" ) ? typePrice : typeYield );
               r.setClose( row.getClose() );
               r.setModifier( modifier );
   
               em.persist( r );
            }
   
            em.getTransaction().commit();
         } catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
         } finally {
            reader.close();
            if ( em.getTransaction().isActive() ) {
               em.getTransaction().rollback();
            }
         }
      }
   }
   
   
   protected void populatePositions( EntityManager em, EntityManager emProduction, String file ) throws IOException, InstantiationException, IllegalAccessException {
      try {
         TypedQuery<Position> qPosition = emProduction.createQuery( "SELECT x FROM Position x", Position.class );
         List<Position> positions = qPosition.getResultList();
         
         em.getTransaction().begin();
         for ( Position x : positions ) {
            x.setModifier( modifier );
            
            em.persist( x );
         }
         em.getTransaction().commit();
      } catch ( Exception eProduction ) {
         System.out.println( "\n\n*** Failed to read the production POSITIONS. ***\n" );
         
         InputStream is = this.getClass().getResourceAsStream( file );
         InputStreamReader isr = new InputStreamReader( is );
         ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );
         Optional d = new Optional( new ParseDate( "MM/dd/yyyy" ) );
         Optional f = new Optional( new ParseDouble() );
         CellProcessor[] processors = new CellProcessor[] { d, null, f, null };
         String jpqlInstrument = String.format( "SELECT x FROM Instrument x WHERE x.name = :name" );
         TypedQuery<Instrument> qInstrument = em.createQuery( jpqlInstrument, Instrument.class );
         //Currency currency = em.find( Currency.class, 1 ); // HARD-CODED
         String jpqlCounterparty = String.format( "SELECT x FROM Account x, Counterparty c WHERE x.id = c.account.id AND c.name = :name" );
         TypedQuery<Account> qAccount = em.createQuery( jpqlCounterparty, Account.class );
   
         try {
            Object o;
            final String[] header = reader.getHeader( true );
   
            em.getTransaction().begin();
            
            while ( ( o = reader.read( PositionCSV.class, header, processors ) ) != null ) {
               PositionCSV row = (PositionCSV) o;
               
               qAccount.setParameter( "name", row.getAccount() );
               qInstrument.setParameter( "name", row.getInstrument() );
   
               Account account = qAccount.getSingleResult();
               Instrument instrument = qInstrument.getSingleResult();
               Position r = new Position();
   
               r.setAccount( account );
               r.setInstrument( instrument );
               r.setTradeDate( row.getDate() );
               r.setPosition( row.getPosition() );
               r.setModifier( modifier );
   
               em.persist( r );
            }
   
            em.getTransaction().commit();
         } catch ( Exception e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
         } finally {
            reader.close();
            if ( em.getTransaction().isActive() ) {
               em.getTransaction().rollback();
            }
         }
      }
   }
   
   
   static public void setJpaProperties( Properties properties ) {
      properties.put( TARGET_DATABASE, "MySQL" );
      properties.put( TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name() );
      properties.put( CLASSLOADER, DDLGenerator.class.getClassLoader() );

      properties.put( JDBC_DRIVER, properties.get( "com.buyside.db.init.core.driver" ) );
      properties.put( JDBC_URL, properties.get( "com.buyside.db.init.core.url" ) );
      properties.put( JDBC_USER, properties.get( "com.buyside.db.init.core.user" ) );
      properties.put( JDBC_PASSWORD, properties.get( "com.buyside.db.init.core.password" ) );

      properties.put( LOGGING_LEVEL, properties.get( "com.buyside.db.init.core.eclipselink.logging.level" ) );
      properties.put( "eclipselink.logging.level.sql", properties.get( "com.buyside.db.init.core.eclipselink.logging.level.sql" ) );
      properties.put( "eclipselink.logging.parameters", properties.get( "com.buyside.db.init.core.eclipselink.logging.parameters" ) );
      properties.put( LOGGING_SESSION, "true" );
      properties.put( LOGGING_THREAD, "true" );
      properties.put( LOGGING_TIMESTAMP, "true" );
   }
   
   
   static public void main( String[] args ) throws InstantiationException, IllegalAccessException, IOException {
      if ( args.length < 2 ) {
         System.err.println( "At least 2 properties files must be specified on the command line.  The first is for " +
         		              "read-only access to the production database and subsequent files are for another " +
         		              "(development) environment.  Each non-production file supercedes the previous." );
         System.exit( -1 );
      }
      
      Properties production = new Properties();
      File file = new File( args[0] );
      
      if ( file.exists() ) {
         System.out.println( "Loading " + file );
         
         production.load( new FileInputStream( file ) );
      } else {
         System.err.println( "Failed to read the read-only production properties file." );
         System.exit( -1 );
      }
      
      Properties properties = new Properties();
      
      for ( String name : args ) {
         if ( name.equals( args[0] ) ) continue;
         
         file = new File( name );
         
         if ( file.exists() ) {
            System.out.println( "Loading " + file );
            
            properties.load( new FileInputStream( file ) );
         }
      }
      
      setJpaProperties( production );
      setJpaProperties( properties );

      properties.put( DDL_GENERATION, "drop-and-create-tables" ); // NOTE

      Scanner in = new Scanner( System.in );
      
      System.out.println( String.format( "You are about to DROP TABLES on %s among others.  If you want to procceed then enter 'confident'.", properties.get( JDBC_URL ) ) );
      
      if ( !in.nextLine().trim().equals( "confident" ) ) {
         System.exit( 0 );
      }
      
      in.close();

      EntityManager em = null, emProduction = null;
      EntityManagerFactory emf = null, emfProduction = null;

      try {
         emf = Persistence.createEntityManagerFactory( properties.get( "com.buyside.db.init.core.pu" ).toString(), properties );
         em = emf.createEntityManager( properties );
         
         emfProduction = Persistence.createEntityManagerFactory( production.get( "com.buyside.db.init.core.pu" ).toString(), production );
         emProduction = emfProduction.createEntityManager( production );

         DDLGenerator generator = new DDLGenerator( em );
         
         generator.populate( em, emProduction );

         System.out.println( "done" );
      } catch ( Exception ex ) {
         ex.printStackTrace();
      } finally {
         if ( em != null ) em.close();
         if ( emf != null ) emf.close();
         if ( emProduction != null ) emProduction.close();
         if ( emfProduction != null ) emfProduction.close();
      }
   }
}
