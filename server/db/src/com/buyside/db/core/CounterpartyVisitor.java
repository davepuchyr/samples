// $Id: CounterpartyVisitor.java 1233 2013-11-08 20:16:50Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;


/**
 * Visitor to deal with the different findBy...() methods of the composite components' services of {@link Counterparty}.
 * 
 * @author dave
 */
public interface CounterpartyVisitor {
   public Account visit( Counterparty composite, Account visitor );

   public Firm visit( Counterparty composite, Firm visitor );

   public Vector visit( Counterparty composite, Vector visitor );
}
