// $Id$
package com.avaritia.app.in3s.client.provider;

import java.util.HashMap;
import java.util.Map;
import com.avaritia.app.in3s.client.Injector;
import com.avaritia.lib.js.sprintf.client.Sprintf;


public class ProviderAdapterFactory {
   static private Map<String, ProviderAdapter> name2adapter = new HashMap<String, ProviderAdapter>();


   static public ProviderAdapter getProviderAdapter( String name ) {
      ProviderAdapter adapter = name2adapter.get( name );

      if ( adapter == null && name != null ) {
         if ( name.equals( "facebook" ) ) { // HARD-CODED
            adapter = Injector.INSTANCE.getFacebookAdapter();
         } else {
            throw new IllegalArgumentException( Sprintf.format( "'%s' is not a know provider.", name ) );
         }

         name2adapter.put( name, adapter );
      }

      return adapter;
   }
}

