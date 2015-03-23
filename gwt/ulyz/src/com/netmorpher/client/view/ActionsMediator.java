// $Id: ActionsMediator.java 101 2010-05-14 20:33:06Z dave $
package com.netmorpher.client.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.puremvc.java.multicore.interfaces.IMediator;
import org.puremvc.java.multicore.interfaces.INotification;
import org.puremvc.java.multicore.patterns.facade.Facade;
import org.puremvc.java.multicore.patterns.mediator.Mediator;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.netmorpher.client.ApplicationFacade;
import com.netmorpher.client.model.ActionsAccountsProxy;
import com.netmorpher.client.model.ActionsSitesProxy;
import com.netmorpher.client.model.ActionsTemplatesProxy;
import com.netmorpher.client.model.FungiblesProxy;
import com.netmorpher.client.model.MappingsProxy;
import com.netmorpher.client.model.vo.ActionVO;

/**
 * @author dave
 *
 */
public class ActionsMediator extends Mediator implements IMediator {
   static public final String NAME = "ActionsMediator";

   private ActionsAccountsProxy _proxyAccounts = null;
   private ActionsSitesProxy _proxySites = null;
   private ActionsTemplatesProxy _proxyTemplates = null;
   private FungiblesProxy _proxyFungibles = null;
   private MappingsProxy _proxyMappings = null;
      
   private HashMap<String, String> _mapFungibleLabelValues = new HashMap<String, String>();
   private HashMap<String, ListBox> _mapFungibleListBoxes = new HashMap<String, ListBox>();
   private HashMap<String, String> _snapshot = null;
   
   // widgets
   private VerticalPanel _root = new VerticalPanel();
   private VerticalPanel _upper = new VerticalPanel();
   private ScrollPanel _lower = new ScrollPanel();
   private HorizontalPanel _layoutBasis = new HorizontalPanel();
   private CaptionPanel _panelSite = new CaptionPanel( "Site" );
   private CaptionPanel _panelAccount = new CaptionPanel( "Account" );
   private CaptionPanel _panelTemplate = new CaptionPanel( "Template" );
   private ListBox _sites = new ListBox();
   private ListBox _accounts = new ListBox();
   private ListBox _templates = new ListBox();
   private HorizontalPanel _layoutPlaceholders = new HorizontalPanel();
   private CaptionPanel _panelSynonyms = new CaptionPanel( "Placeholder Mappings" );
   private FlexTable _tableFungibles = new FlexTable();
   private VerticalPanel _layoutSnapshot = new VerticalPanel();
   private HorizontalPanel _buttonsSnapshot = new HorizontalPanel();
   private Button _bShowKeywords = new Button( "show placeholders" );
   private Button _replace = new Button( "replace placeholders" );
   private Button _publish = new Button( "publish" );
   private CaptionPanel _panelSnapshot = new CaptionPanel( "Snapshot" );
   private FlexTable _tableSnapshot = new FlexTable();


   public ActionsMediator()  {
      super( NAME, null );

      setViewComponent( _root );
   }

   
   @Override
   public String[] listNotificationInterests() {
      return new String[] {
         ApplicationFacade.ACTIONS_ACCOUNTS_UPDATED,
         ApplicationFacade.ACTIONS_SITE_SELECTED,
         ApplicationFacade.ACTIONS_SITES_UPDATED,
         ApplicationFacade.ACTIONS_TEMPLATES_UPDATED,
         ApplicationFacade.FUNGIBLES_UPDATED,
         ApplicationFacade.INITIALIZE,
         ApplicationFacade.MAPPINGS_UPDATED,
         ApplicationFacade.ON_RESIZE,
         ApplicationFacade.SITES_UPDATED,
         ApplicationFacade.SHOW_TEMPLATE,
         ApplicationFacade.SNAPSHOT_UPDATED
      };
   }

   
   @Override
   public void handleNotification( INotification notification ) {
      String notice = notification.getName();
      
      if ( notice.equals( ApplicationFacade.ACTIONS_TEMPLATES_UPDATED ) ) {
         _templates.clear();
         
         for ( int i = 0, n = _proxyTemplates.size(); i < n; ++i ) {
            _templates.addItem( _proxyTemplates.get( i ).getTemplate() );
         }
         
         _templates.addItem( "" );
         _templates.setSelectedIndex( _templates.getItemCount() - 1 );         
      } else if ( notice.equals( ApplicationFacade.SHOW_TEMPLATE ) ) {
         ArrayList<String> fields = _proxyMappings.getKeys();
         for ( int i = 0, n = fields.size(); i < n; ++i ) {
            String field = fields.get( i );
            _tableSnapshot.setText( i, 0, field );
            _tableSnapshot.setText( i, 1, _proxyMappings.getValue( field ) );
         }
      } else if ( notice.equals( ApplicationFacade.ACTIONS_ACCOUNTS_UPDATED ) ) {
         _accounts.clear();
                  
         for ( int i = 0, n = _proxyAccounts.size(); i < n; ++i ) {
            _accounts.addItem( _proxyAccounts.get( i ).getUser() );
         }
         
         _accounts.addItem( "" );
         _accounts.setSelectedIndex( _accounts.getItemCount() - 1 );
      } else if ( notice.equals( ApplicationFacade.ACTIONS_SITES_UPDATED ) ) {
         _sites.clear();
         
         for ( int i = 0, n = _proxySites.size(); i < n; ++i ) {
            _sites.addItem( _proxySites.get( i ).getSite() );
         }
         
         _sites.addItem( "" );
         _sites.setSelectedIndex( _sites.getItemCount() - 1 );
      } else if ( notice.equals( ApplicationFacade.MAPPINGS_UPDATED ) ) {
         ArrayList<String> fields = _proxyMappings.getKeys();
         int n = fields.size(), i = _tableSnapshot.getRowCount() - n;
         while ( --i >= 0 ) _tableSnapshot.removeRow( 0 );
         
         for ( i = 0; i < n; ++i ) {
            String field = fields.get( i );
            _tableSnapshot.setText( i, 0, field );
            _tableSnapshot.setText( i, 1, _proxyMappings.getValue( field ) );
         }
      } else if ( notice.equals( ApplicationFacade.SNAPSHOT_UPDATED ) ) {
         ActionVO vo = (ActionVO) notification.getBody();
         _snapshot = vo.getTemplate();
         ArrayList<String> fields = _proxyMappings.getKeys();
         
         for ( int i = 0, n = fields.size(); i < n; ++i ) {
            String field = fields.get( i );
            _tableSnapshot.setText( i, 0, field );
            _tableSnapshot.setText( i, 1, _snapshot.get( field ) );
         }

         _publish.setEnabled( !vo.isDirty() );
      } else if ( notice.equals( ApplicationFacade.FUNGIBLES_UPDATED ) ) {
         _mapFungibleLabelValues.clear();
         _mapFungibleListBoxes.clear();
         
         int i = _tableFungibles.getRowCount();
         
         while ( --i >= 0 ) _tableFungibles.removeRow( i );
         
         for ( String key : _proxyFungibles.getKeys() ) {
            if ( key.indexOf( "{{" ) != 0 || key.indexOf( "}}" ) != key.length() - 2 ) continue; // HARD-CODED delimiters
            
            ++i; // i == -1 at first

            Widget w = null;
            ArrayList<String> fungibles = _proxyFungibles.getValue( key );
            
            if ( fungibles.size() == 1 ) {
               String value = fungibles.get( 0 );
               _mapFungibleLabelValues.put( key, value );
               w = new Label( value );
            } else {
               ListBox lb = new ListBox();
               for ( String synonym : fungibles ) {
                  lb.addItem( synonym.substring( 0, Math.min( 30, synonym.length() ) ), synonym ); // dmjp
               }
               _mapFungibleListBoxes.put( key, lb );
               w = lb;
            }

            _tableFungibles.setText( i, 0, key );
            _tableFungibles.setWidget( i, 1, w );
         }

         _replace.setEnabled( true );
      } else if ( notice.equals( ApplicationFacade.INITIALIZE ) ) {
         initialize();
      }

      super.handleNotification( notification );
   }
   
   
   public void initialize() {
      final Facade facade = this.getFacade();

      _proxyAccounts = (ActionsAccountsProxy) facade.retrieveProxy( ActionsAccountsProxy.NAME );
      _proxyFungibles = (FungiblesProxy) facade.retrieveProxy( FungiblesProxy.NAME );
      _proxyMappings = (MappingsProxy) facade.retrieveProxy( MappingsProxy.NAME );
      _proxySites = (ActionsSitesProxy) facade.retrieveProxy( ActionsSitesProxy.NAME );
      _proxyTemplates = (ActionsTemplatesProxy) facade.retrieveProxy( ActionsTemplatesProxy.NAME );
      
      _panelSite.add( _sites );
      _panelAccount.add( _accounts );
      _panelTemplate.add( _templates );
      
      _layoutBasis.add( _panelSite );
      _layoutBasis.add( _panelAccount );
      _layoutBasis.add( _panelTemplate );
      
      _buttonsSnapshot.add( _bShowKeywords );
      _buttonsSnapshot.add( _replace );
      _buttonsSnapshot.add( _publish );
      
      _layoutSnapshot.add( _buttonsSnapshot );
      _layoutSnapshot.add( _tableSnapshot );
      
      _panelSynonyms.add( _tableFungibles );
      _panelSnapshot.add( _layoutSnapshot );
      
      _layoutPlaceholders.add( _panelSynonyms );
      _layoutPlaceholders.add( _panelSnapshot );
      
      _upper.add( _layoutBasis );
      _upper.add( _layoutPlaceholders );
      
      _root.add( _upper );
      _root.add( _lower );
      
      _sites.addChangeHandler( new ChangeHandler( ) {
         public void onChange( ChangeEvent event ) {
            _publish.setEnabled( false );
            facade.sendNotification( ApplicationFacade.ACTIONS_SITE_SELECTED );
         }
      } );
      
      _accounts.addChangeHandler( new ChangeHandler( ) {
         public void onChange( ChangeEvent event ) {
            _publish.setEnabled( false );
            facade.sendNotification( ApplicationFacade.ACTIONS_ACCOUNT_SELECTED );
         }
      } );
      
      _templates.addChangeHandler( new ChangeHandler( ) {
         public void onChange( ChangeEvent event ) {
            _publish.setEnabled( false );
            facade.sendNotification( ApplicationFacade.ACTIONS_TEMPLATE_SELECTED );
         }
      } );
      
      _bShowKeywords.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            facade.sendNotification( ApplicationFacade.SHOW_TEMPLATE );
         }
      } );
      
      _replace.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            HashMap<String, String> mappings = new HashMap<String, String>();
            HashMap<String, String> synonyms = new HashMap<String, String>();
            ActionVO vo = new ActionVO( mappings, synonyms );
            
            for ( String key : _proxyMappings.getKeys() ) {
               mappings.put( key, _proxyMappings.getValue( key ) );
            }
            
            for ( String key : _mapFungibleLabelValues.keySet() ) {
               synonyms.put( key, _mapFungibleLabelValues.get( key ) );
            }

            for ( String key : _mapFungibleListBoxes.keySet() ) {
               ListBox lb = _mapFungibleListBoxes.get( key );
               int selected = lb.getSelectedIndex();
               String value = selected != -1 ? lb.getValue( selected ) : key;
               synonyms.put( key, value );
            }
            
            facade.sendNotification( ApplicationFacade.SUBSTITUTE_KEYWORDS, vo );
         }
      } );
      
      _publish.setEnabled( false );
      _publish.addClickHandler( new ClickHandler() {
         public void onClick( ClickEvent event ) {
            String target = getSiteString() + "/" + getAccountString() + "/" + getTemplateString(); // HARD-CODED
            Window.open( "about:blank", target, "" );

            VerticalPanel panel = new VerticalPanel();
            FormPanel form = new FormPanel( target );
            form.setWidget( panel );
            form.setMethod( FormPanel.METHOD_POST );
            form.setAction( "/x/" + getSiteString() + ".pl" ); // HARD-CODED
            
            for ( String key : _snapshot.keySet() ) {
               TextBox tb = new TextBox();
               tb.setName( key );
               tb.setValue( _snapshot.get( key ) );
               panel.add( tb );
            }
            
            _lower.setWidget( form );

            facade.sendNotification( ApplicationFacade.PUBLISH, form );
         }
      } );
   }

   
   public int getSite() {
      return _sites.getSelectedIndex();
   }
               
               
   public int getAccount() {
      return _accounts.getSelectedIndex();
   }
               
               
   public int getTemplate() {
      return _templates.getSelectedIndex();
   }

               
   public String getSiteString() {
      return _sites.getItemText( _sites.getSelectedIndex() );
   }
   
   
   public String getAccountString() {
      int account = _accounts.getSelectedIndex();
      return account != -1 ? _accounts.getItemText( account ) : "";
   }
   
   
   public String getTemplateString() {
      return _templates.getItemText( _templates.getSelectedIndex() );
   }
}

