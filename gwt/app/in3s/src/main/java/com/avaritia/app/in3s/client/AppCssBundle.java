// $Id$
package com.avaritia.app.in3s.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.CssResource.NotStrict;
import com.googlecode.mgwt.ui.client.widget.panel.PanelAppearance.PanelCss;

/**
 * @see <a href=https://groups.google.com/d/msg/mgwt/89APPrGuKe8/7ooFOEmp9PoJ>This</a> seems like a hack but there's not up-to-date
 *      docs on mgwt style
 */
public interface AppCssBundle extends ClientBundle {
   public static final AppCssBundle INSTANCE = GWT.create( AppCssBundle.class );


   public interface Css extends CssResource {
      @ClassName( "backgroundFacebook" ) String backgroundFacebook();

      @ClassName( "backgroundGoogle" ) String backgroundGoogle();

      @ClassName( "backgroundLinkedIn" ) String backgroundLinkedIn();

      @ClassName( "backgroundMail" ) String backgroundMail();

      @ClassName( "backgroundPhone" ) String backgroundPhone();

      @ClassName( "backgroundPinterest" ) String backgroundPinterest();

      @ClassName( "backgroundSkype" ) String backgroundSkype();

      @ClassName( "backgroundTwitter" ) String backgroundTwitter();

      @ClassName( "backgroundYahoo" ) String backgroundYahoo();

      @ClassName( "buttonZingable" ) String buttonZingable();
   }


   @Import( { PanelCss.class } ) // import necessary mgwt css scope so we can use these classes in our css file
   @Source( "app.css" )
   @NotStrict // required due to @Import
   Css css();
}
