// $Id: ActionsSitesProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import com.netmorpher.client.model.vo.SiteVO;

/**
 * @author dave
 *
 */
public class ActionsSitesProxy extends DatabaseProxy<SiteVO> {
   public static final String NAME = "ActionsSitesProxys";


   public ActionsSitesProxy() {
      super( NAME );
   }
}

