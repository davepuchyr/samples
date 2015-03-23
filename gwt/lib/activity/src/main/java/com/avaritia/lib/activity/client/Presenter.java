// $Id$
package com.avaritia.lib.activity.client;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;


public interface Presenter {
   void start( final AcceptsOneWidget panel, final EventBus eventBus );

   void stop();
}
