// $Id: ActionsTemplatesProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import com.netmorpher.client.model.vo.TemplateVO;

/**
 * @author dave
 *
 */
public class ActionsTemplatesProxy extends DatabaseProxy<TemplateVO> {
   public static final String NAME = "ActionsTemplatesProxy";


   public ActionsTemplatesProxy() {
      super( NAME );
   }
}

