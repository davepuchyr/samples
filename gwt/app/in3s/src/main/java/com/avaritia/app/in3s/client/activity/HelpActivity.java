// $Id$
package com.avaritia.app.in3s.client.activity;

import com.avaritia.app.in3s.client.place.HelpPlace;
import com.avaritia.app.in3s.client.presenter.HelpPresenter;
import com.avaritia.lib.activity.client.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Singleton;


@Singleton
public class HelpActivity extends Activity<HelpPresenter> {
   public void setPlace( HelpPlace place ) {
      presenter.setPlace( place );
   }


   @Override public void start( AcceptsOneWidget panel, EventBus eventBus ) {
      presenter.start( panel, eventBus );
   }
}
