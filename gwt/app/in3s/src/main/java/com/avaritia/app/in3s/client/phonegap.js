// $Id$

app.phonegap = function( oArgs ) {
   var onBack = oArgs.onBack
     , onForward = oArgs.onForward
     , onHelp = oArgs.onHelp
     , navigation = d3.select( "#navigation" )
     , width
     , height
     // constants
   ;

   // HACK: fake css3 support with js
   navigation.selectAll( "a" ).each( function( d, i ) {
      app.nthOfTypeButtonA( d3.select( this ), i );
   } );
   // ~HACK

   navigation
      .style( "top", "10px" ) // HARD-CODED
      .style( "left", "10px" )
   ;

   navigation.select( "#back" ).on( "click", onBack );
   navigation.select( "#forward" ).on( "click", onForward );
   navigation.select( "#help" ).on( "click", onHelp );
}
