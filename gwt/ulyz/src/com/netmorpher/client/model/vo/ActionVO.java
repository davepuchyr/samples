// $Id: ActionVO.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.model.vo;

import java.util.HashMap;


/**
 * @author dave
 *
 */
public class ActionVO {
   private HashMap<String, String> _template = null;
   private HashMap<String, String> _synonyms = null;
   boolean _dirty = true;
   
   
   public ActionVO( HashMap<String, String> template, HashMap<String, String> synonyms ) {
      _template = template;
      _synonyms = synonyms;
   }


   public ActionVO( HashMap<String, String> template, boolean dirty ) {
      _template = template;
      _dirty = dirty;
   }


   public HashMap<String, String> getTemplate() {
      return _template;
   }
   
   
   public HashMap<String, String> getSynonyms() {
      return _synonyms;
   }
   
   
   public boolean isDirty() {
      return _dirty;
   }
}
