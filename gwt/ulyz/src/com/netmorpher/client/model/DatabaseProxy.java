// $Id: DatabaseProxy.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.model;

import java.util.ArrayList;

import org.puremvc.java.multicore.interfaces.IProxy;
import org.puremvc.java.multicore.patterns.proxy.Proxy;

/**
 * @author dave
 *
 */
public class DatabaseProxy<T> extends Proxy implements IProxy {
   private ArrayList<T> _arrayList = null;
   
   
   @SuppressWarnings( "unchecked" )
   public DatabaseProxy( String proxyName ) {
      super( proxyName, new ArrayList<T>() );
      _arrayList = (ArrayList<T>) this.getData();
   }
   
   
   public T get( int i ) {
      return 0 <= i && i < _arrayList.size() ? _arrayList.get( i ) : null;
   }
   
   
   public int size() {
      return _arrayList.size();
   }
}

