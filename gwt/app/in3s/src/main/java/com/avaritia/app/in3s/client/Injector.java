// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.app.in3s.client.provider.FacebookAdapter;
import com.avaritia.app.in3s.shared.ClientModule;
import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.googlecode.gwtphonegap.client.PhoneGap;


@GinModules( ClientModule.class )
public interface Injector extends Ginjector {
   static final public Injector INSTANCE = GWT.create( Injector.class );

   // mandatory (top-level) injections
   public App getApp();

   public PhoneGap getPhoneGap();

   // optional (dynamic) injections
   public FacebookAdapter getFacebookAdapter();
}
