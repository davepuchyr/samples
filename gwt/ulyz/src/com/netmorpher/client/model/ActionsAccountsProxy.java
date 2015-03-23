// $Id: ActionsAccountsProxy.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.model;

import com.netmorpher.client.model.vo.AccountVO;

/**
 * @author dave
 *
 */
public class ActionsAccountsProxy extends DatabaseProxy<AccountVO> {
   public static final String NAME = "ActionsAccountsProxy";


   public ActionsAccountsProxy() {
      super( NAME );
   }
}

