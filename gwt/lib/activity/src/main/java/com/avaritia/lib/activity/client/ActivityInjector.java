package com.avaritia.lib.activity.client;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;


public class ActivityInjector<P extends Place, A extends Activity> {
   /**
    * Injected {@link Activity} associated with {@Place}.
    */
   @Inject protected A activity;


   /**
    * Returns the {@link Activity} associated with {@Place} place.
    *
    * @param place
    * @return default activity that is not a function of place
    */
   public A getActivity( P place ) {
      return activity;
   }
}