// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.app.in3s.client.activity.AuthenticatedActivity;
import com.avaritia.app.in3s.client.activity.AuthorizedActivity;
import com.avaritia.app.in3s.client.activity.D3Activity;
import com.avaritia.app.in3s.client.activity.HelpActivity;
import com.avaritia.app.in3s.client.activity.HomeActivity;
import com.avaritia.app.in3s.client.activity.PhoneGapActivity;
import com.avaritia.app.in3s.client.activity.SettingsActivity;
import com.avaritia.app.in3s.client.activity.UnauthenticatedActivity;
import com.avaritia.app.in3s.client.activity.UnauthorizedActivity;
import com.avaritia.app.in3s.client.place.AuthenticatedPlace;
import com.avaritia.app.in3s.client.place.AuthorizedPlace;
import com.avaritia.app.in3s.client.place.D3Place;
import com.avaritia.app.in3s.client.place.HelpPlace;
import com.avaritia.app.in3s.client.place.HomePlace;
import com.avaritia.app.in3s.client.place.PhoneGapPlace;
import com.avaritia.app.in3s.client.place.PlaceAdapter;
import com.avaritia.app.in3s.client.place.PlaceAdapterVisitor;
import com.avaritia.app.in3s.client.place.SettingsPlace;
import com.avaritia.app.in3s.client.place.UnauthenticatedPlace;
import com.avaritia.app.in3s.client.place.UnauthorizedPlace;
import com.avaritia.lib.activity.client.ActivityInjector;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class ActivityMapper implements com.google.gwt.activity.shared.ActivityMapper, PlaceAdapterVisitor {
   @Inject private ActivityInjector<AuthenticatedPlace, AuthenticatedActivity> authenticatedActivityInjector;

   @Inject private ActivityInjector<AuthorizedPlace, AuthorizedActivity> authorizedActivityInjector;

   @Inject private ActivityInjector<D3Place, D3Activity> d3ActivityInjector;

   @Inject private ActivityInjector<HelpPlace, HelpActivity> helpActivityInjector;

   @Inject private ActivityInjector<HomePlace, HomeActivity> homeActivityInjector;

   @Inject private ActivityInjector<PhoneGapPlace, PhoneGapActivity> phoneGapActivityInjector;

   @Inject private ActivityInjector<SettingsPlace, SettingsActivity> settingsActivityInjector;

   @Inject private ActivityInjector<UnauthenticatedPlace, UnauthenticatedActivity> unauthenticatedActivityInjector;

   @Inject private ActivityInjector<UnauthorizedPlace, UnauthorizedActivity> unauthorizedActivityInjector;


   @Override public Activity getActivity( final Place place ) {
      assert( place instanceof PlaceAdapter );

      return ( (PlaceAdapter) place ).accept( this );
   }


   @Override public Activity visit( AuthenticatedPlace place ) {
      return authenticatedActivityInjector.getActivity( place );
   }


   @Override public Activity visit( AuthorizedPlace place ) {
      return authorizedActivityInjector.getActivity( place );
   }


   @Override public Activity visit( D3Place place ) {
      return d3ActivityInjector.getActivity( place );
   }


   @Override public Activity visit( HelpPlace place ) {
      HelpActivity activity = helpActivityInjector.getActivity( place );

      activity.setPlace( place );

      return activity;
   }


   @Override public Activity visit( HomePlace place ) {
      return homeActivityInjector.getActivity( place );
   }


   @Override public Activity visit( PhoneGapPlace place ) {
      return phoneGapActivityInjector.getActivity( place );
   }


   @Override public Activity visit( SettingsPlace place ) {
      return settingsActivityInjector.getActivity( place );
   }


   @Override public Activity visit( UnauthenticatedPlace place ) {
      return unauthenticatedActivityInjector.getActivity( place );
   }


   @Override public Activity visit( UnauthorizedPlace place ) {
      return unauthorizedActivityInjector.getActivity( place );
   }
}
