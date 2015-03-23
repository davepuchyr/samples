// $Id: AccountVO.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author dave
 *
 */
public class AccountVO extends JavaScriptObject {
   protected AccountVO() {
   }
   
   
   // JSNI methods
   public final native String getSite() /*-{ return this.site; }-*/;
   public final native String getUser() /*-{ return this.user; }-*/;
   public final native String getPassword() /*-{ return this.password; }-*/;
}
