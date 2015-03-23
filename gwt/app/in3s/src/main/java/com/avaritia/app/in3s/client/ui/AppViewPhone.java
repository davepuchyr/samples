// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.app.in3s.client.AnimatingActivityManager;
import com.avaritia.app.in3s.client.AnimationMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.googlecode.mgwt.ui.client.widget.animation.AnimationWidget;


@Singleton
public class AppViewPhone extends com.avaritia.app.in3s.client.AppView {
   //final static private Logger LOGGER = Logger.getLogger( AppView.class.getName() );

   interface Binder extends UiBinder<Widget, AppViewPhone> {
   }

   @UiField SimplePanel panel;


   public AppViewPhone() {
      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );
   }


   @Override public void bind() {
      AnimationWidget animationWidget = new AnimationWidget();

      AnimationMapper animationMapper = GWT.create( AnimationMapper.class );

      AnimatingActivityManager activityManager = new AnimatingActivityManager( activityMapper, animationMapper, eventBus, true );

      activityManager.setDisplay( animationWidget );

      panel.add( animationWidget );
   }
}
