// $Id: Currency.java 1233 2013-11-08 20:16:50Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.buyside.common.BuySideEntity;
import com.buyside.db.CompositeEntityAcceptor;

/**
 * The persistent class for the CURRENCIES database table.
 * 
 */
@Entity
@Table( name = "CURRENCIES" )
public class Currency extends BuySideEntity implements CompositeEntityAcceptor<InstrumentVisitor, Instrument, Currency> {
   private static final long serialVersionUID = 3812116161676701567L;
   
   private Integer id;
   private String code;
   private String name;
   private String symbol;


   public Currency() {
   }


   @Id
   @GeneratedValue( strategy = GenerationType.TABLE ) // all attempts to get +1 increments failed except for IDENTITY, but that has major drawbacks
   @Column( name = "ID", nullable = false )
   public Integer getId() {
      return id;
   }


   public void setId( Integer id ) {
      this.id = id;
   }


   @Column( name = "NAME" )
   public String getName() {
      return this.name;
   }


   public void setName( String name ) {
      this.name = name;
   }


   @NotNull
   @Size( min = 3, max = 3 )
   @Column( name = "CODE", unique = true )
   public String getCode() {
      return code;
   }


   public void setCode( String code ) {
      this.code = code;
   }


   @Column( name = "SYMBOL" )
   public String getSymbol() {
      return symbol;
   }


   public void setSymbol( String symbol ) {
      this.symbol = symbol;
   }


   @Override public String getToken() {
      return String.valueOf( id );
   }


   @Override public Currency accept( InstrumentVisitor visitor, Instrument instrument ) {
      return visitor.visit( instrument, this );
   }
}
