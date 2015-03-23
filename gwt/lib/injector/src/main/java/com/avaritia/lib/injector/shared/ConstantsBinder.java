// $Id$
package com.avaritia.lib.injector.shared;

import static com.google.inject.name.Names.named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.google.gwt.inject.client.binder.GinBinder;


/**
 * Client-side binder of constants.
 */
public class ConstantsBinder implements ConstantsStreamBinder<GinBinder> {
   @Override public void bind( GinBinder binder, InputStream is ) {
      Properties properties = new Properties();

      try {
         properties.load( is );
      } catch ( IOException e ) {
         e.printStackTrace();
      }

      for ( Object oName : properties.keySet() ) {
         Object oValue = properties.get( oName );
         String sName = oName.toString();
         String sValue = oValue.toString().trim();
         int dot = sName.lastIndexOf( '.' );
         String name = sName.substring( 0, dot ).trim();
         String type = sName.substring( dot + 1 ).trim();

         if ( type.equals( "Boolean" ) ) {
            binder.bindConstant().annotatedWith( named( name ) ).to( Boolean.parseBoolean( sValue ) );
         } else if ( type.equals( "Double" ) ) {
            binder.bindConstant().annotatedWith( named( name ) ).to( Double.parseDouble( sValue ) );
         } else if ( type.equals( "Float" ) ) {
            binder.bindConstant().annotatedWith( named( name ) ).to( Float.parseFloat( sValue ) );
         } else if ( type.equals( "Integer" ) ) {
            binder.bindConstant().annotatedWith( named( name ) ).to( Integer.parseInt( sValue ) );
         } else if ( type.equals( "Long" ) ) {
            binder.bindConstant().annotatedWith( named( name ) ).to( Long.parseLong( sValue ) );
         } else {
            binder.bindConstant().annotatedWith( named( name ) ).to( sValue );
         }
      }
   }
}

