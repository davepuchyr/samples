// $Id$

app.help = function( oArgs ) {
   var onBack = oArgs.onBack
     , scrollPanel = d3.select( "#" + oArgs.scrollPanelId )
     , navigation = d3.select( "#navigation" )
   ;

   // HACK: deal with a single (first-of-type and last-of-type) button
   navigation.select( "a" )
      .style( "border-radius", "7px" )
   ;
   // ~HACK

   d3.select( "#back" ).on( "click", onBack );
   d3.select( window ).on( "resize", resize );

   function resize() {
      width = parseInt( scrollPanel.style( "width" ), 10 );
      height = parseInt( scrollPanel.style( "height" ), 10 );

      if ( height < 320 ) height = app.hackHeight( scrollPanel ); // HACK

      var navigationn = navigation.selectAll( "a" ).data().length // HACK: can't use bcr since we're using clever css
        , navigationw = navigationn * buttondim + navigationn - 1

      navigation
         .style( "top", "10px" ) // dmjp: HARD-CODED
         .style( "left", width - navigationw - 10 + "px" ) // dmjp: HARD-CODED 10
      ;
   }

   resize();
};

