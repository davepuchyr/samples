// $Id: DatabaseReadCommand.java 43 2009-12-30 15:27:19Z dave $
package com.netmorpher.client.controller;

import java.util.ArrayList;

import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.command.SimpleCommand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.DatabaseProxy;

/**
 * @author dave
 *
 */
public abstract class DatabaseReadCommand<T> extends SimpleCommand {
   protected abstract String getSQL( final INotification notification );
   protected abstract String getProxyName();
   protected abstract String getNotificationName();

   
   @SuppressWarnings( "unchecked" )
   protected void onSuccess( JsArray<?> rows ) {
      ApplicationFacade facade = ApplicationFacade.getInstance();
      DatabaseProxy<T> proxy = (DatabaseProxy<T>) facade.retrieveProxy( getProxyName() );
      ArrayList<T> a = (ArrayList<T>) proxy.getData();
      
      a.clear();
      
      for ( int i = 0, n = rows.length(); i < n; ++i ) {
         a.add( (T) rows.get( i ) );
      }
      
      facade.sendNotification( getNotificationName() );
   }

   
   protected void onFailure( String msg ) {
      Window.alert( msg );
   }
   
   
   private final native JsArray<?> getArray( String json ) /*-{ return eval( "(" + json + ")" ); }-*/;

   
   public void execute( final INotification notification ) {
      String sql = getSQL( notification );
      
      if ( sql == null ) return; // derived classes that don't have enough info for the query could return null
      GWT.log( sql, null );

      RequestBuilder builder = new RequestBuilder( RequestBuilder.POST, "/x/db2json.pl" ); // dmjp

      try {
         builder.sendRequest( sql, new RequestCallback() {
            public void onError( Request request, Throwable exception ) {
               onFailure( "Couldn't retrieve JSON " + exception );
            }

            public void onResponseReceived( Request request, Response response ) {
               if ( 200 == response.getStatusCode() ) {
                  GWT.log( response.getText(), null );
                  onSuccess( getArray( response.getText() ) );
               } else {
                  onFailure( "Couldn't retrieve JSON (" + response.getStatusText() + ")" );
               }
            }
         } );
      } catch ( RequestException e ) {
         onFailure( "Couldn't retrieve JSON: " + e.getMessage() );
      }
   }
}
