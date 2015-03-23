// $Id$

app.home = function( oArgs ) {
   var onForward = oArgs.onForward
     , onHelp = oArgs.onHelp
     , svgWrapperId = oArgs.svgWrapperId
     , wrapper = d3.select( "#" + svgWrapperId )
     , navigation = wrapper.select( "#navigation" )
     , svg = wrapper.select( "svg" )
     , super3 = svg.select( "#super3" )
     , ins = svg.select( "#ins" )
     , width, height // set in resize()
     // constants
   ;

   // HACK: fake css3 support with js
   navigation.selectAll( "a" ).each( function( d, i ) {
      app.nthOfTypeButtonA( d3.select( this ), i );
   } );
   // ~HACK

   navigation.select( "#forward" ).on( "click", onForward );
   navigation.select( "#help" ).on( "click", onHelp );
   d3.select( window ).on( "resize", resize );

   function resize() {
      width = parseInt( wrapper.style( "width" ), 10 );
      height = parseInt( wrapper.style( "height" ), 10 );

      if ( height < 320 ) height = app.hackHeight( wrapper ); // HACK

      svg
         .attr( "width", width )
         .attr( "height", height )
      ;

      // font-sizes
      var fontSize = Math.round( width / 9 ); // 3 chars "ins" in the middle 1/3rd of the width == 9

      ins
         .style( "font-size", fontSize + "px" )
         .attr( "x", width / 2 )
         .attr( "y", height / 2 )
      ;

      super3.style( "font-size", fontSize / 2 + "px" );

      // positions
      var bcrIns = ins.node().getBoundingClientRect()
        , bcrSuper3 = super3.node().getBoundingClientRect()
      ;

      super3
         .attr( "x", bcrIns.left + bcrIns.width * 2 / 3 )
         .attr( "y", bcrIns.top )
      ;

      // navigation
      var navigationn = navigation.selectAll( "a" ).data().length // HACK: can't use bcr since we're using clever css
        , navigationw = navigationn * buttondim + navigationn - 1

      navigation
         .style( "top", "10px" ) // HARD-CODED
         .style( "left", "10px" );
      ;
   }

   resize();
};

