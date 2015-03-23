// $Id$
package com.avaritia.lib.place.shared;

import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;


public class PlaceHistoryHandler<H extends Historian> extends com.google.gwt.place.shared.PlaceHistoryHandler {
   @Inject public PlaceHistoryHandler( PlaceHistoryMapper mapper, H fsm ) {
      super( mapper, fsm );
   }
}
