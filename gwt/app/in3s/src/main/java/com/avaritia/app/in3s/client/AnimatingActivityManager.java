// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.lib.console.client.Console;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.mgwt.mvp.client.AnimationMapper;
import com.googlecode.mgwt.mvp.client.MGWTAnimationEndEvent;
import com.googlecode.mgwt.mvp.client.MGWTAnimationEndHandler;
import com.googlecode.mgwt.mvp.client.MGWTAnimationStartEvent;
import com.googlecode.mgwt.mvp.client.MGWTAnimationStartHandler;


public class AnimatingActivityManager extends com.googlecode.mgwt.mvp.client.AnimatingActivityManager {
   private boolean symmetric = true;


   public AnimatingActivityManager( ActivityMapper mapper, AnimationMapper animationMapper, EventBus eventBus, boolean fireAnimationEvents ) {
      super( mapper, animationMapper, eventBus );

      setFireAnimationEvents( fireAnimationEvents );

      eventBus.addHandler( MGWTAnimationStartEvent.TYPE, new MGWTAnimationStartHandler() {
         @Override public void onAnimationStartHandler( MGWTAnimationStartEvent event ) {
            symmetric = !symmetric;

            Console.debug( "AnimatingActivityManager.MGWTAnimationStartHandler(): symmetric == %s;", symmetric );
         }
      } );

      eventBus.addHandler( MGWTAnimationEndEvent.TYPE, new MGWTAnimationEndHandler() {
         @Override public void onAnimationEnd( MGWTAnimationEndEvent event ) {
            symmetric = !symmetric;

            Console.debug( "AnimatingActivityManager.MGWTAnimationEndHandler(): symmetric == %s;", symmetric );
         }
      } );
   }


   @Override public void onPlaceChange( PlaceChangeEvent event ) {
      Console.warn( "AnimatingActivityManager.onPlaceChange(): new place == %s;", event.getNewPlace().getClass().getSimpleName() );

      super.onPlaceChange( event );
   }
}
