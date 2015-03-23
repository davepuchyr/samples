// $Id: AccountsMediator.java 97 2010-05-13 17:57:57Z dave $
package com.netmorpher.client.view;

import java.util.ArrayList;

import org.puremvc.java.multicore.interfaces.IMediator;
import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.facade.Facade;
import org.puremvc.java.multicore.patterns.mediator.Mediator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.AccountsProxy;
import com.netmorpher.client.model.vo.DatabaseWriteVO;

/**
 * @author dave
 *
 */
public class AccountsMediator extends Mediator implements IMediator {
   static public final String NAME = "AccountsMediator";

   private AccountsProxy _proxyAccounts = null;
   private int _selected = -1;
   
   // widgets
   private CaptionPanel _root = new CaptionPanel( "Accounts" );
   private VerticalPanel _layout = new VerticalPanel();
   private HorizontalPanel _buttons = new HorizontalPanel();
   private Button _bAdd = new Button( "Add" );
   private Button _bEdit = new Button( "Edit" );
   private Button _bDelete = new Button( "Delete" );
   private FlexTable _table = new FlexTable();

   // dialog widgets
   private VerticalPanel _dialog = new VerticalPanel();
   private TextArea _text = new TextArea();
   private HorizontalPanel _footer = new HorizontalPanel();
   private Button _commit = new Button( "Commit" );
   private Button _dismiss = new Button( "Dismiss" );


   public AccountsMediator()  {
      super( NAME, null );
      
      setViewComponent( _root );
   }

   
   @Override
   public String[] listNotificationInterests() {
      return new String[] {
         ApplicationFacade.INITIALIZE,
         ApplicationFacade.ON_RESIZE,
         ApplicationFacade.ACCOUNTS_UPDATED
      };
   }

   
   @Override
   public void handleNotification( INotification notification ) {
      String notice = notification.getName();
      
      if ( notice.equals( ApplicationFacade.ACCOUNTS_UPDATED ) ) {
         _bEdit.setVisible( true );

         ArrayList<String> keys = _proxyAccounts.getKeys();
         int i = _table.getRowCount() - keys.size();
         while ( --i >= 0 ) _table.removeRow( 0 );
         
         i = 0;
         
         for ( String key : keys ) {
            _table.setText( i, 0, key );
            _table.setText( i, 1, _proxyAccounts.getPassword( key ) );
            ++i;
         }
      } else if ( notice.equals( ApplicationFacade.INITIALIZE ) ) {
         initialize();
      }

      super.handleNotification( notification );
   }
   
   
   public void initialize() {
      final Facade facade = this.getFacade();
      
      _proxyAccounts = (AccountsProxy) facade.retrieveProxy( AccountsProxy.NAME );
      
      _buttons.add( _bAdd );
      _buttons.add( _bEdit );
      _buttons.add( _bDelete );

      _layout.add( _buttons );
      _layout.add( _table );
      
      _root.add( _layout );
      
      _table.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            int row = _table.getCellForEvent( event ).getRowIndex();
            if ( row == _selected ) return;

            _bEdit.setEnabled( true );
            _bDelete.setEnabled( true );

            RowFormatter formatter = _table.getRowFormatter();
            formatter.addStyleName( row, "selectedRow" );
            if ( _selected >= 0 ) formatter.removeStyleName( _selected, "selectedRow" );
            _selected = row;
         }
      } );
      
      _bAdd.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            if ( _selected >= 0 ) _table.getRowFormatter().removeStyleName( _selected, "selectedRow" );
            _selected = -1;

            _text.setValue( "" );

            facade.sendNotification( ApplicationFacade.DIALOG_SHOW, _dialog );
         }
      } );

      _bEdit.setEnabled( false );
      _bEdit.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            String key = _proxyAccounts.getKeys().get( _selected );

            _text.setValue( _proxyAccounts.getPassword( key ) );

            facade.sendNotification( ApplicationFacade.DIALOG_SHOW, _dialog );
         }
      } );

      _bDelete.setEnabled( false );
      _bDelete.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            DatabaseWriteVO vo = _proxyAccounts.getDatabaseWriteVO( _selected, null );
            
            facade.sendNotification( ApplicationFacade.DATABASE_WRITE, vo );

            _bEdit.setEnabled( false );
            _bDelete.setEnabled( false );
            _table.getRowFormatter().removeStyleName( _selected, "selectedRow" );
            _selected = -1;
         }
      } );

      // dialog
      _footer.add( _commit );
      _footer.add( _dismiss );
      
      _dialog.add( _text );
      _dialog.add( _footer );
      
      _commit.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            facade.sendNotification( ApplicationFacade.DIALOG_HIDE );
            
            DatabaseWriteVO vo = _proxyAccounts.getDatabaseWriteVO( _selected, _text.getValue() );

            facade.sendNotification( ApplicationFacade.DATABASE_WRITE, vo );
         }
      } );
      
      _dismiss.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            facade.sendNotification( ApplicationFacade.DIALOG_HIDE );
         }
      } );
   }
}

