// $Id: DatabaseWriteCommand.java 34 2009-12-19 18:42:32Z dave $
package com.netmorpher.client.controller;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.vo.DatabaseWriteVO;

/**
 * @author dave
 *
 */
public class DatabaseWriteCommand extends SimpleCommand {
   public void execute( final INotification notification ) {
      DatabaseWriteVO vo = (DatabaseWriteVO) notification.getBody();
      RequestBuilder builder = new RequestBuilder( RequestBuilder.POST, "/x/json2db.pl" ); // dmjp
      final String notice = vo.getNotice();
      GWT.log( vo.getJSON(), null );

      try {
         builder.sendRequest( vo.getJSON(), new RequestCallback() {
            public void onError( Request request, Throwable exception ) {
               onFailure( "Request failed: " + exception );
            }

            public void onResponseReceived( Request request, Response response ) {
               GWT.log( response.getText(), null );

               if ( 200 == response.getStatusCode() ) {
                  ApplicationFacade.getInstance().sendNotification( notice );
               } else {
                  onFailure( "Bad response: " + response.getStatusText() );
               }
            }
         } );
      } catch ( RequestException e ) {
         onFailure( "Request exception: " + e.getMessage() );
      }
   }

   
   protected void onFailure( String msg ) {
      Window.alert( msg );
   }
}
