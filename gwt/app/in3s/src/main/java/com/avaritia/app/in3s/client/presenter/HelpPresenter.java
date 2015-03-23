// $Id$
package com.avaritia.app.in3s.client.presenter;

import com.avaritia.app.in3s.client.HelpBundle;
import com.avaritia.app.in3s.client.place.HelpPlace;
import com.avaritia.app.in3s.client.ui.PendingPopup;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


@Singleton
public class HelpPresenter extends PresenterImpl implements HelpView.Presenter {
   @Inject private HelpView view;

   @Inject private PendingPopup pendingPopup;

   private boolean helpChanged;

   private String[] values;


   public void setPlace( HelpPlace place ) {
      helpChanged = values != place.getValues();

      if ( helpChanged ) values = place.getValues();
   }


   @Override public void start( final AcceptsOneWidget panel, EventBus eventBus ) {
      this.eventBus = eventBus;

      view.setPresenter( this );

      panel.setWidget( view );

      if ( helpChanged ) {
         pendingPopup.show();

         final String[] tokens = values;

         Scheduler.get().scheduleDeferred( new Command() {
            @Override public void execute() {
               String title = tokens[0];
               String body = HelpBundle.Helper.getBody( tokens[1] ); // this can take a while so wrap it in a deferred command

               view.setTitle( title );
               view.setBody( body );

               pendingPopup.hide( true );
            }
         } );
      }
   }


   @Override public void stop() {
      eventBus = null;
   }


   @Override public String getHelpToken() {
      return null; // no help on help
   }


   @Override public void onHelp() {
      // no-op
   }
}
