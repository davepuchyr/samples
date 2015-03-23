// $Id$
//# sourceURL=/home/dave/src/gwt/lib/provider/src/main/java/com/avaritia/lib/provider/client/provider.js

var provider = {}; // HARD-CODED in conjunction with ProviderBundleGenerator.java


provider.facebook = function( oArgs ) {
   var FB = window.FB;

   var isAuthenticated = false;
   var isAuthorized = false;
   var cache = {
      permissions : [],
      authorizations : [] // requested authorizations
   };

   function get( endpoint, callback, options ) {
      var url = '/';

      if ( endpoint == 'me' ) {
         url += endpoint;
      } else {
         url += 'me/' + endpoint;
      }

      FB.api( url, options, function( response ) {
         if ( !response.error ) {
            //console.log( 'get', endpoint, response );

            cache[endpoint] = response.data ? response.data : response;

            if ( callback ) callback( response );
         } else {
            console.error( 'get', endpoint, response );
         }
      } );
   }

   function getPermissions( callback ) {
      get( 'permissions', callback );
   }

   function hasPermission() {
      if ( cache.authorizations.length != cache.permissions.length ) return false; // short-circuit

      for ( var j in cache.authorizations ) {
         var permission = cache.authorizations[j];

         for ( var i in cache.permissions ) {
            var permissions = cache.permissions[i];

            if ( permissions.permission == permission && permissions.status != 'granted' ) {
               return false;
            }
         }
      }

      return true;
   }

   function cachePermissions() {
      if ( isAuthenticated ) {
         getPermissions( function() {
            isAuthorized = hasPermission();
         } );
      }
   }

   function onFacebookChange( response, callback ) {
      console.log( 'onFacebookChange', response );

      isAuthenticated = response.status == 'connected';

      if ( isAuthenticated ) {
         getPermissions( function() {
            isAuthorized = hasPermission();

            if ( callback ) callback();
         } );
      } else {
         isAuthorized = false;

         if ( callback ) callback();
      }
   }

   function authenticate( scope ) {
      function callback( response ) {
         onFacebookChange( response, provider.facebook.onAuthenticate );
      }

      FB.login( callback, {
         scope : scope,
         return_scopes : true
      } );
   }

   function authorize( scope ) {
      cache.authorizations = scope.split( /,/g );

      function callback( response ) {
         onFacebookChange( response, provider.facebook.onAuthorize );
      }

      FB.login( callback, {
         scope : scope,
         return_scopes : true
      } );
   }

   // import callbacks
   provider.facebook.onAuthenticate = oArgs.onAuthenticate;
   provider.facebook.onAuthorize    = oArgs.onAuthorize;

   // export functions
   provider.facebook.authenticate    = authenticate;
   provider.facebook.authorize       = authorize;
   provider.facebook.get             = get;
   provider.facebook.isAuthenticated = function() { return isAuthenticated; };
   provider.facebook.isAuthorized    = function() { return isAuthorized; };

   FB.Event.subscribe( 'auth.authResponseChange', onFacebookChange );
   FB.Event.subscribe( 'auth.statusChange', onFacebookChange );
};
