// $Id$
package com.avaritia.lib.activity.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/**
 * An abstract {@link Activity} that delegates to an injected default {@link Presenter}. Subclasses can add their own presenters so
 * that mutliple presenters are provided to a given activity.
 *
 * @param <P> the default {@link Presenter} class
 */
abstract public class Activity<P extends Presenter> implements com.google.gwt.activity.shared.Activity {
   @Inject protected P presenter;


   @Override public String mayStop() {
      return null;
   }


   @Override public void onCancel() {
   }


   @Override public void onStop() {
      presenter.stop();
   }


   @Override public void start( AcceptsOneWidget panel, EventBus eventBus ) {
      presenter.start( panel, eventBus );
   }
}
