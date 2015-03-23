// $Id: Counterparty.java 1237 2013-11-13 19:58:07Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.buyside.common.BuySideEntity;
import com.buyside.db.CompositeEntityAcceptor;

/**
 * The persistent class for the COUNTERPARTIES database table.
 */
@Entity
@Table( name = "COUNTERPARTIES" )
public class Counterparty extends BuySideEntity {
   private static final long serialVersionUID = 4148798796195095954L;

   private Integer id;
   
   protected String name;
   
   protected Account account;
   
   protected Firm firm;
   
   protected Vector vector;
   

   @Transient
   public CompositeEntityAcceptor<CounterpartyVisitor, Counterparty, ?> getUnderlying() {
      // don't try to use ? operator because that makes junit tests die
      if ( account != null ) return account;
      if ( firm != null ) return firm;
      if ( vector != null ) return vector;
      
      return null;
   }
   
   
   @Transient
   public String getJpqlColumn() {
      if ( account != null ) return "account";
      if ( firm != null ) return "firm";
      if ( vector != null ) return "vector";
      
      return null;
   }
   
   
   public Counterparty() {
   }
   
   
   public Counterparty( Account account, Firm firm, Vector vector ) {
      this.account = account;
      this.firm = firm;
      this.vector = vector;
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


   @NotNull
   @Size( min = 1 )
   @Column( name = "NAME", unique = true, nullable = false  )
   public String getName() {
      return this.name;
   }


   public void setName( String name ) {
      this.name = name;
   }


   @JoinColumn( name = "ACCOUNT", insertable = false, updatable = true ) // must be updatable (writable) in some form or another to satisfy eclipselink
   public Account getAccount() {
      return this.account;
   }


   public void setAccount( Account account ) {
      this.account = account;
   }


   @JoinColumn( name = "FIRM", insertable = false, updatable = true ) // must be updatable (writable) in some form or another to satisfy eclipselink
   public Firm getFirm() {
      return this.firm;
   }


   public void setFirm( Firm firm ) {
      this.firm = firm;
   }


   @JoinColumn( name = "VECTOR", insertable = false, updatable = true ) // must be updatable (writable) in some form or another to satisfy eclipselink
   public Vector getVector() {
      return this.vector;
   }


   public void setVector( Vector vector ) {
      this.vector = vector;
   }


   @Override public String getToken() {
      return id != null ? id.toString() : null;
   }
}
