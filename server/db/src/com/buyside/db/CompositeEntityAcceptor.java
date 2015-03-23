// $Id: CompositeEntityAcceptor.java 1230 2013-11-06 22:41:26Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db;

/**
 * Acceptor of the visitor pattern to deal with the different findBy...() methods of the composite components' services of
 * {@link Counterparty}, eg {@link Account}, {@link Vector}, etc. See {@link CounterpartyVisitor}.
 * 
 * @author dave
 *
 * @param <Visitor> 
 * @param <Composite> wrapper class composed of composite properties of which only one is not null
 * @param <Derived> non-null composite member
 */
public interface CompositeEntityAcceptor<Visitor, Composite, Derived> {
   public Integer getId();

   public Derived accept( Visitor visitor, Composite composite );
}

