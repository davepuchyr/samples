package com.avaritia.app.in3s.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Provides the lowest common denominator of injection members needed by all derived {@link AppView}s in order to overcome Gin's
 * lack of recursion up the injectee's class hierarchy, particularly when deferred binding is in effect.
 */
abstract public class AppView extends Composite {
   /**
    * Binds the injected members together, if need be.
    */
   abstract public void bind();


   /**
    * The {@link ActivityMapper} for the main content used by all form factors.
    */
   @Inject protected ActivityMapper activityMapper;


   /**
    * The one and only {@link EventBus}.
    */
   @Inject protected EventBus eventBus;
}
