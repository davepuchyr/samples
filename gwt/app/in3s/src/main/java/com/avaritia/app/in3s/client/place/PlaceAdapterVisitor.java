// $Id$
package com.avaritia.app.in3s.client.place;

import com.google.gwt.activity.shared.Activity;


public interface PlaceAdapterVisitor extends com.avaritia.lib.place.client.PlaceAdapterVisitor {
   public Activity visit( AuthenticatedPlace place );

   public Activity visit( AuthorizedPlace place );

   public Activity visit( D3Place place );

   public Activity visit( HelpPlace place );

   public Activity visit( HomePlace place );

   public Activity visit( PhoneGapPlace place );

   public Activity visit( SettingsPlace place );

   public Activity visit( UnauthenticatedPlace place );

   public Activity visit( UnauthorizedPlace place );
}

