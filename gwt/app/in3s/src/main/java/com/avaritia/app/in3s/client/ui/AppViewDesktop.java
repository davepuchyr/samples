// $Id$
package com.avaritia.app.in3s.client.ui;

import com.avaritia.app.in3s.client.AnimatingActivityManager;
import com.avaritia.app.in3s.client.AnimationMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.googlecode.mgwt.ui.client.widget.animation.AnimationWidget;


@Singleton
public class AppViewDesktop extends com.avaritia.app.in3s.client.AppView {
   //final static private Logger LOGGER = Logger.getLogger( AppView.class.getName() );

   interface Binder extends UiBinder<Widget, AppViewDesktop> {
   }

   @UiField VerticalPanel west;
   @UiField SimplePanel center;
   @UiField SimplePanel east;


   public AppViewDesktop() {
      //Console.log( GWT.getModuleName() + ".AppView.AppView(): about to initWidget()" );

      initWidget( GWT.<Binder> create( Binder.class ).createAndBindUi( this ) );

      //Console.log( GWT.getModuleName() + ".AppView.AppView(): did initWidget()" );
   }


   @Override public void bind() {
      // content
      AnimationWidget contentWidget = new AnimationWidget();

      AnimationMapper animationMapper = GWT.create( AnimationMapper.class );

      AnimatingActivityManager activityManager = new AnimatingActivityManager( activityMapper, animationMapper, eventBus, true );

      activityManager.setDisplay( contentWidget );

      // sidebar
      /* dmjp
      AnimationWidget sidebarWidget = new AnimationWidget();

      animationMapper = GWT.create( AnimationMapper.class );

      activityManager = new AnimatingActivityManager( activityMapperSidebar, animationMapper, eventBus, false );

      activityManager.setDisplay( sidebarWidget );
      */

      // wire 'em up
      west.add( new Label( "navigationView" ) ); // dmjp
      center.add( contentWidget );
      //east.add( sidebarWidget );

      center.getElement().setId( "dmjp-center" );
      east.getElement().setId( "dmjp-east" );
   }
}
