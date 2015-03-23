// $Id$
package com.avaritia.app.zingers.shared;

import com.avaritia.lib.server.domain.Locator;
import com.avaritia.lib.server.domain.User;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;


@ProxyFor( value = User.class, locator = Locator.class )
public interface UserProxy extends EntityProxy {
   Long getId();

   String getFacebook();
   String getMail();
   String getName();
   String getPhone();

   void setFacebook( String providerId );
   void setMail( String providerId );
   void setName( String name );
   void setPhone( String providerId );
}
