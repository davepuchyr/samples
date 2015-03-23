// $Id$
package com.avaritia.app.in3s.client;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Inject;


public class PlaceHistoryHandler extends com.avaritia.lib.place.shared.PlaceHistoryHandler<FiniteStateMachine> {
   @Inject public PlaceHistoryHandler( PlaceHistoryMapper mapper, FiniteStateMachine fsm ) {
      super( mapper, fsm );
   }
}
