// $Id$
package com.avaritia.app.in3s.client;

import com.avaritia.lib.js.jsni.client.JsBundle;
import com.google.gwt.core.client.Callback;


abstract public class AppBundle extends JsBundle {
   @Override protected abstract void inject( Callback<Void, Exception> callback );


   // HACK: order of injection in determined in subclasses
   final static protected String app      = "app.js";
   final static protected String d3       = "d3.js";
   final static protected String help     = "help.js";
   final static protected String home     = "home.js";
   final static protected String phonegap = "phonegap.js";
}
