// $Id: TradesMitTest.java 1228 2013-11-06 16:18:25Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.pnl;

import org.junit.Test;
import com.buyside.db.core.EntityManagerBase;


/**
 * @author dave
 * 
 */
public class TradesMitTest extends EntityManagerBase {
   @Test public void testTradesMit() {
      TypedQuery<User> queryTrader = em.createQuery( "SELECT p FROM User p WHERE p.name = :name", User.class );
      queryTrader.setParameter( "name", "Dave" );
      User trader = (User) queryTrader.getSingleResult();
      
      TypedQuery<Book> queryBook = em.createQuery( "SELECT p FROM Book p WHERE p.name = :name", Book.class );
      queryBook.setParameter( "name", "JE_MAN1" );
      Book JE_MAN1 = (Book) queryBook.getSingleResult();
      queryBook.setParameter( "name", "KK_MAN1" );
      Book KK_MAN1 = (Book) queryBook.getSingleResult();

      TypedQuery<Market> queryMarket = em.createQuery( "SELECT p FROM Market p WHERE p.name = :name", Market.class );
      queryMarket.setParameter( "name", "Internal" );
      Market marketInternal = (Market) queryMarket.getSingleResult();
      queryMarket.setParameter( "name", "eSpeed" );
      Market eSpeed = (Market) queryMarket.getSingleResult();
      
      TypedQuery<Firm> queryFirm = em.createQuery( "SELECT p FROM Firm p WHERE p.name = :name", Firm.class );
      queryFirm.setParameter( "name", "Cantor" );
      Firm firmCantor = (Firm) queryFirm.getSingleResult();      

      Book bookNull = Book.getNullBook( em );
      Firm firmNull = Firm.getNullFirm( em );
      CounterpartyPK  pkJE_MAN1 = new CounterpartyPK( JE_MAN1.getId(), firmNull.getId() );
      Counterparty counterJE_MAN1 = em.find( Counterparty.class, pkJE_MAN1 );
      CounterpartyPK pkCantor = new CounterpartyPK( bookNull.getId(), firmCantor.getId() );
      Counterparty cantor = em.find( Counterparty.class, pkCantor );

      ReportType report = em.find( ReportType.class, "SOR_TRADE_REPORT" );
      ExecType reject = em.find( ExecType.class, "Amend Reject" );
      ExecType etTrade = em.find( ExecType.class, "Trade" );
      TradeType journal = em.find( TradeType.class, "JOURNAL_ENTRY" );
      TradeType gui = em.find( TradeType.class, "EXTERNAL_GUI_TRADE" );
      
      long millis = System.currentTimeMillis() - 86400 * 1000;
      Date td = new Date( millis );
      Date sd = new Date( millis + 86400 * 1000 );

      TradeMit internalMIT = new TradeMit(); 
      internalMIT.setBook( KK_MAN1 );
      internalMIT.setCounterparty( counterJE_MAN1 );
      internalMIT.setMarket( marketInternal );
      internalMIT.setReportType( report );
      internalMIT.setExecType( reject );
      internalMIT.setTradeType( journal );
      internalMIT.setTrader( trader );
      
      internalMIT.setPrice( 101.25 );
      internalMIT.setQuantity( 69.1 );
      internalMIT.setSymbol( "GT10" );
      internalMIT.setTradeDate( td );
      internalMIT.setSettleDate( sd );
      internalMIT.setTradeTime( new Time( millis ) );
      internalMIT.setTransactionTime( new Time( millis - 100 ) );

      try {
         em.getTransaction().begin();
         em.persist( internalMIT );
         em.getTransaction().commit();
      } catch ( Exception ex ) {
         if ( em.getTransaction().isActive() ) em.getTransaction().rollback();
         ex.printStackTrace();
         fail( "Exception during testPersistence()." );
      }

      millis = System.currentTimeMillis();
      td = new Date( millis );
      sd = new Date( millis + 86400 * 1000 * 3 );

      TradeMit externalMIT = new TradeMit(); 
      externalMIT.setBook( KK_MAN1 );
      externalMIT.setCounterparty( cantor );
      externalMIT.setMarket( eSpeed );
      externalMIT.setReportType( report );
      externalMIT.setExecType( etTrade );
      externalMIT.setTradeType( gui );
      externalMIT.setTrader( trader );
      
      externalMIT.setPrice( 101.123478676 );
      externalMIT.setQuantity( -769.1 );
      externalMIT.setSymbol( "GT30" );
      externalMIT.setTradeDate( td );
      externalMIT.setSettleDate( sd );
      externalMIT.setTradeTime( new Time( millis ) );
      externalMIT.setTransactionTime( new Time( millis - 100 ) );

      try {
         em.getTransaction().begin();
         em.persist( externalMIT );
         em.getTransaction().commit();
      } catch ( Exception ex ) {
         if ( em.getTransaction().isActive() ) em.getTransaction().rollback();
         ex.printStackTrace();
         fail( "Exception during testPersistence()." );
      }
   }
}
