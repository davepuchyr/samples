// $Id: TemplatesTemplateMediator.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.view;

import org.puremvc.java.multicore.interfaces.IMediator;
import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.facade.Facade;
import org.puremvc.java.multicore.patterns.mediator.Mediator;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.netmorpher.client.ApplicationFacade;

/**
 * @author dave
 *
 */
public class TemplatesTemplateMediator extends Mediator implements IMediator {
   static public final String NAME = "TemplatesTemplateMediator";

   // widgets
   private HorizontalPanel _root = new HorizontalPanel();


   public TemplatesTemplateMediator()  {
      super( NAME, null );
      
      setViewComponent( _root );
   }

   
   @Override
   public String[] listNotificationInterests() {
      return new String[] {
         ApplicationFacade.INITIALIZE,
         ApplicationFacade.ON_RESIZE
      };
   }

   
   @Override
   public void handleNotification( INotification notification ) {
      String notice = notification.getName();

      if ( notice.equals( ApplicationFacade.INITIALIZE ) ) {
         initialize();
      }
      
      super.handleNotification( notification );
   }
   
   
   public void initialize() {
      final Facade facade = getFacade();

      _root.add( (Widget) facade.retrieveMediator( TemplatesMediator.NAME ).getViewComponent() );
      _root.add( (Widget) facade.retrieveMediator( TemplateMediator.NAME ).getViewComponent() );
   }
}

