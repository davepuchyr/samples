// $Id: SynonymVO.java 35 2009-12-22 12:58:24Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author dave
 *
 */
public class MappingVO extends JavaScriptObject {
   protected MappingVO() {
   }
   
   
   // JSNI methods
   public final native String getField() /*-{ return this.field; }-*/;
   public final native String getValue() /*-{ return this.value; }-*/;
}
