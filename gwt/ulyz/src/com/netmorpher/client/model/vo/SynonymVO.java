// $Id: SynonymVO.java 35 2009-12-22 12:58:24Z dave $
package com.netmorpher.client.model.vo;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author dave
 *
 */
public class SynonymVO extends JavaScriptObject {
   protected SynonymVO() {
   }
   
   
   // JSNI methods
   public final native String getKeyword() /*-{ return this.keyword; }-*/;
   public final native String getSynonyms() /*-{ return this.synonyms; }-*/;
}
