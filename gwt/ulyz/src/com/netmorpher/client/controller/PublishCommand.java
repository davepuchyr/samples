// $Id: PublishCommand.java 103 2010-05-17 13:44:21Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.google.gwt.user.client.ui.FormPanel;


/**
 * @author dave
 *
 */
public class PublishCommand extends SimpleCommand {
   public void execute( final INotification notification ) {
      FormPanel form = (FormPanel) notification.getBody();
      form.submit();
      //Window.alert( "submitted " + form.getTarget() + " at " + new Date() );
      form.removeFromParent();
      form = null;
   }
}
