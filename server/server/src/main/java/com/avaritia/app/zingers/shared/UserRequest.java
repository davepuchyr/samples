// $Id$
package com.avaritia.app.zingers.shared;

import com.avaritia.lib.server.service.ServiceLocator;
import com.avaritia.lib.server.service.UserService;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;


@Service( value = UserService.class, locator = ServiceLocator.class )
public interface UserRequest extends RequestContext {
   Request<UserProxy> findByProvider( String provider, String providerUserId );

   Request<UserProxy> onLogin( String provider, UserProxy proxy );
}
