// $Id: AccountsProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.puremvc.java.multicore.patterns.facade.Facade;

import com.google.gwt.core.client.JsArray;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.AccountVO;
import com.netmorpher.client.model.vo.DatabaseWriteVO;
import com.netmorpher.client.model.vo.DeleteVO;
import com.netmorpher.client.model.vo.InsertVO;
import com.netmorpher.client.model.vo.UpdateVO;
import com.netmorpher.client.view.SitesMediator;

/**
 * @author dave
 *
 */
public class AccountsProxy extends DatabaseProxy<AccountVO> {
   public static final String NAME = "AccountsProxy";

   private ArrayList<String> _keys = new ArrayList<String>();
   private HashMap<String, String> _map = new HashMap<String, String>();


   public AccountsProxy() {
      super( NAME );
   }
   
   
   public void map( JsArray<AccountVO> rows ) {
      _keys.clear();
      _map.clear();

      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         AccountVO vo = rows.get( i );
         String key = vo.getUser();
         
         _keys.add( key );
         _map.put( key, vo.getPassword() );
      }
   }
   

   public final ArrayList<String> getKeys() {
      return _keys;
   }
   
   
   public final String getPassword( String key ) {
      return _map.get( key );
   }
   
   
   public DatabaseWriteVO getDatabaseWriteVO( int selected, String value ) {
      String id = 0 <= selected && selected < size() ? get( selected ).getUser() : null;
      Facade facade = getFacade();
      SitesProxy proxy = (SitesProxy) facade.retrieveProxy( SitesProxy.NAME );
      SitesMediator mediator = (SitesMediator) facade.retrieveMediator( SitesMediator.NAME );
      String site = proxy.get( mediator.getSelected() ).getSite();

      if ( id != null && value == null ) return new DeleteVO( ApplicationFacade.ACCOUNTS_CHANGED, "accounts", "site = '" + site + "' AND user = '" + id + "'" );

      InsertVO ivo = null;
      ArrayList<String> cols = new ArrayList<String>();
      
      if ( id != null ) {
         cols.add( "password" );
         ivo = new UpdateVO( ApplicationFacade.ACCOUNTS_CHANGED, "accounts", cols, "site = '" + site + "' AND user = '" + id + "'" );
         ivo.put( "password", value );
      } else {      
         cols.add( "site" );
         cols.add( "user" );
         ivo = new InsertVO( ApplicationFacade.ACCOUNTS_CHANGED, "accounts", cols );
         ivo.put( "user", value );
         ivo.put( "site", site );
      }
      
      return ivo;
   }
}

