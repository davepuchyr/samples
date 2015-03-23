// $Id: Firm.java 1233 2013-11-08 20:16:50Z dpuchyr_nac@OPTIONS-IT.COM $
package com.buyside.db.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import com.buyside.common.BuySideEntity;
import com.buyside.db.CompositeEntityAcceptor;


/**
 * The persistent class for the FIRMS database table.
 */
@Entity
@Table( name = "FIRMS" )
public class Firm extends BuySideEntity implements CompositeEntityAcceptor<CounterpartyVisitor, Counterparty, Firm> {
   private static final long serialVersionUID = 7831001433162800111L;
   
	private Integer id;
	private String name;

   
   public Firm( Integer id, String name ) {
      this.id = id;
      this.name = name;
   }


   public Firm() {
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

   @Column( name = "NAME", unique = true ) // must be case sensitive for BTEC and BTec, but I couldn't get columnDefinition to work; hacked DDLGenerator
   @Size( min = 1, max = 255 )
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
   @Override public String getToken() {
      return id != null ? id.toString() : null;
   }


   @Override public Firm accept( CounterpartyVisitor visitor, Counterparty composite ) {
      visitor.visit( composite, this );
      
      return this;
   }
}
