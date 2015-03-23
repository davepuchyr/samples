// $Id: DialogMediator.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.view;

import org.puremvc.java.multicore.interfaces.IMediator;
import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.mediator.Mediator;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.netmorpher.client.ApplicationFacade;

/**
 * @author dave
 *
 */
public class DialogMediator extends Mediator implements IMediator {
   static public final String NAME = "DialogMediator";
   
   private DialogBox _root = new DialogBox();


   public DialogMediator() {
      super( NAME, null );
   }


   public String[] listNotificationInterests() {
      return new String[] {
            ApplicationFacade.DIALOG_HIDE,
            ApplicationFacade.DIALOG_SHOW
      };
   }

   
   public void handleNotification( INotification notification ) {
      String notice = notification.getName();
      
      if ( notice.equals( ApplicationFacade.DIALOG_SHOW ) ) {
         Widget w = (Widget) notification.getBody();

         _root.setWidget( w );
         _root.setModal( true );
         _root.center();
         _root.show();
      } else if ( notice.equals( ApplicationFacade.DIALOG_HIDE ) ) {
         _root.hide();
      }

      super.handleNotification( notification );
   }
   
   
   public void setTitle( String title ) {
      _root.setTitle( title );
   }
}

