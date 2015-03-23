// $Id: HistoryVO.java 31 2009-12-18 17:09:45Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author dave
 *
 */
public class HistoryVO extends JavaScriptObject {
   protected HistoryVO() {
   }
   
   
   // JSNI methods
   public final native String getMark() /*-{ return this.mark; }-*/;
   public final native String getContent() /*-{ return this.content; }-*/;
}
