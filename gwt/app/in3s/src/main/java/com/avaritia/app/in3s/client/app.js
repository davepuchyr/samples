// $Id$

var app = {}; // HARD-CODED in conjunction with AppBundleGenerator.java

var d3 = window.d3;
var provider = window.provider;

var maxdim = 200 // HARD-CODED; images have been scaled to 200x200 as of 2014.11.17
  , buttondim = 50 // HARD-CODED in conjunction with app.css .button a width: 50px
  , buttonxoffset = 51 // HARD-CODED in conjunction with nth-of-type in app.css
;

app.facebook = function( oArgs ) {
   // import callbacks
   app.facebook.onMe = oArgs.onMe;

   function callback( response ) {
       //console.log( response );

       app.facebook.onMe( response );
   }

   // export functions
   app.facebook.me = function() { provider.facebook.get( "me", callback ); }
};


app.hackHeight = function( selection ) {
   var height = parseInt( selection.style( "height" ), 10 )
     , height0 = height
   ;

   while ( height < 320 ) { // HACK: ipad
      selection = d3.select( selection.node().parentNode );
      height = parseInt( selection.style( "height" ), 10 );
   }

   console.warn( "app.hackHeight(): height0 == " + height0 + "; height == " + height );

   return height;
}


// HACK: fake css3 support for class button with js
app.nthOfTypeButtonA = function( selection, n ) {
   selection.style( "left", n * buttonxoffset + "px" );
}

