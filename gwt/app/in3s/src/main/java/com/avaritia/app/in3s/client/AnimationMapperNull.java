// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.lib.console.client.Console;
import com.google.gwt.place.shared.Place;
import com.googlecode.mgwt.ui.client.widget.animation.Animation;


public class AnimationMapperNull extends AnimationMapper {
   @Override public Animation getAnimation( Place oldPlace, Place newPlace ) {
      Console.debug( AnimationMapperNull.class.getSimpleName() );

      return null;
   }
}
