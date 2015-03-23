// $Id: SiteVO.java 98 2010-05-14 12:04:32Z dave $
package com.netmorpher.client.model.vo;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author dave
 *
 */
public class SiteVO extends JavaScriptObject {
   protected SiteVO() {
   }
   
   
   public final ArrayList<String> getFields() {
      return new ArrayList<String>( Arrays.asList( joinFields().split( "\\|" ) ) ); // HARD-CODED delimiter
   }
   
   
   // JSNI methods
   public final native String getSite() /*-{ return this.site; }-*/;
   public final native String getDriver() /*-{ return this.driver; }-*/;
   public final native String getFieldsString() /*-{ return this.fields; }-*/;
   public final native String joinFields() /*-{
      if ( !this.joined ) {
         var a = this.fields.split( /,/g ), n = a.length; // HARD-CODED regex
         
         while ( --n >= 0 ) a[n] = a[n].replace( /^\s+/, '' ).replace( /\s+$/, '' );
      
         this.joined = a.join( "|" ); // HARD-CODED delimiter; hack to bridge js to java
      }
      
      return this.joined;
   }-*/;
}
