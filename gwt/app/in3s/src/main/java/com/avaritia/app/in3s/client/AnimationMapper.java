// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.app.in3s.client.place.D3Place;
import com.avaritia.app.in3s.client.place.HelpPlace;
import com.avaritia.app.in3s.client.place.PhoneGapPlace;
import com.avaritia.app.in3s.client.place.SettingsPlace;
import com.avaritia.app.in3s.client.place.UnauthenticatedPlace;
import com.avaritia.app.in3s.client.place.UnauthorizedPlace;
import com.avaritia.lib.console.client.Console;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.ui.client.widget.animation.Animation;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;


public class AnimationMapper implements com.googlecode.mgwt.mvp.client.AnimationMapper {
   //static private final Logger LOGGER = Logger.getLogger( AnimationMapper.class.getName() );


   @Override public Animation getAnimation( Place oldPlace, Place newPlace ) {
      if ( oldPlace == null ) return null;

      // handle help...
      if ( newPlace instanceof HelpPlace ) return Animations.SLIDE_UP_REVERSE;

      if ( oldPlace instanceof HelpPlace ) return Animations.SLIDE_UP;

      // ...then others
      if ( newPlace instanceof D3Place ) return Animations.SLIDE;

      if ( oldPlace instanceof D3Place ) return Animations.SLIDE_REVERSE;

      if ( newPlace instanceof SettingsPlace ) return Animations.SLIDE_UP;

      if ( oldPlace instanceof SettingsPlace ) return Animations.SLIDE_UP_REVERSE;

      if ( newPlace instanceof PhoneGapPlace ) return Animations.SLIDE;

      if ( oldPlace instanceof PhoneGapPlace ) return Animations.SLIDE_REVERSE;

      if ( newPlace instanceof UnauthenticatedPlace ) return Animations.SLIDE;

      if ( oldPlace instanceof UnauthenticatedPlace ) return Animations.SLIDE_REVERSE;

      if ( newPlace instanceof UnauthorizedPlace ) return Animations.SLIDE;

      if ( oldPlace instanceof UnauthorizedPlace ) return Animations.SLIDE_REVERSE;

      Console.log( GWT.getModuleName() + ".AnimationMapper.getAnimation(): using default animation of null" );

      return null;
   }
}
