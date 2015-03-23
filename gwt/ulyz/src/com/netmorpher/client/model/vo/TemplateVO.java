// $Id: TemplateVO.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author dave
 *
 */
public class TemplateVO extends JavaScriptObject {
   protected TemplateVO() {
   }
   
   
   // JSNI methods
   public final native String getTemplate() /*-{ return this.template; }-*/;
   public final native String getField() /*-{ return this.field; }-*/;
   public final native String getValue() /*-{ return this.value; }-*/;
}
