// $Id$
package com.avaritia.lib.place.shared;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


@Singleton
public class PlaceController extends com.google.gwt.place.shared.PlaceController {
   @Inject public PlaceController( EventBus eventBus ) {
      super( eventBus );
   }
}
