// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.app.in3s.client.place.AuthenticatedPlace;
import com.avaritia.app.in3s.client.place.AuthorizedPlace;
import com.avaritia.app.in3s.client.place.D3Place;
import com.avaritia.app.in3s.client.place.HelpPlace;
import com.avaritia.app.in3s.client.place.HomePlace;
import com.avaritia.app.in3s.client.place.PhoneGapPlace;
import com.avaritia.app.in3s.client.place.SettingsPlace;
import com.avaritia.app.in3s.client.place.UnauthenticatedPlace;
import com.avaritia.app.in3s.client.place.UnauthorizedPlace;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * This interface is the hub of your application's navigation system. It links the {@link com.google.gwt.place.shared.Place Place}s
 * your user navigates to with the browser history system &mdash; that is, it makes the browser's back and forth buttons work for
 * you, and also makes each spot in your app bookmarkable.
 * <p>
 * Its implementation is code generated based on the @WithTokenizers annotation.
 */
@WithTokenizers( {
     AuthenticatedPlace.Tokenizer.class
   , AuthorizedPlace.Tokenizer.class
   , D3Place.Tokenizer.class
   , HelpPlace.Tokenizer.class
   , HomePlace.Tokenizer.class
   , PhoneGapPlace.Tokenizer.class
   , SettingsPlace.Tokenizer.class
   , UnauthenticatedPlace.Tokenizer.class
   , UnauthorizedPlace.Tokenizer.class
} )
public interface PlaceHistoryMapper extends com.google.gwt.place.shared.PlaceHistoryMapper {
   static final public String PREFIX = "";

   static final public String PREFIX_AUTHENTICATED   = PREFIX + "authenticated";
   static final public String PREFIX_AUTHORIZED      = PREFIX + "authorized";
   static final public String PREFIX_D3              = PREFIX + "d3";
   static final public String PREFIX_HOME            = PREFIX + "home";
   static final public String PREFIX_HELP            = PREFIX + "help";
   static final public String PREFIX_PHONEGAP        = PREFIX + "phonegap";
   static final public String PREFIX_SETTINGS        = PREFIX + "settings";
   static final public String PREFIX_UNAUTHENTICATED = PREFIX + "unauthenticated";
   static final public String PREFIX_UNAUTHORIZED    = PREFIX + "unauthorized";
}
