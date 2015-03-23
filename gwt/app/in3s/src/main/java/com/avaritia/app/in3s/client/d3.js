// $Id$

app.d3 = function( oArgs ) {
   var onBack = oArgs.onBack
     , onHelp = oArgs.onHelp
     , wrapper = d3.select( "#" + oArgs.svgWrapperId )
     , navigation = wrapper.select( "#navigation" )
     , svg = wrapper.select( "svg" )
     , providers = [ "facebook", "google", "linkedin", "mail", "phone", "pinterest", "skype", "twitter", "yahoo" ]
     , selected = void 0
     , nodes = []
     // set in resize()
     , width
     , height
     , focusSelected
     , focusDefault
     // constants
   ;

   // HACK: fake css3 support with js
   navigation.selectAll( "a" ).each( function( d, i ) {
      app.nthOfTypeButtonA( d3.select( this ), i );
   } );
   // ~HACK

   navigation.select( "#back" ).on( "click", onBack )
   navigation.select( "#help" ).on( "click", onHelp )
   navigation
      .style( "top", "10px" ) // HARD-CODED
      .style( "left", "10px" )
   ;

   // nuke unavailable providers...
   d3.selectAll( "g" )
      .filter( function( d ) {
         var provider = d3.select( this ).attr( "provider" );

         return providers.indexOf( provider ) == -1;
      } )
      .remove()
   ;

   // ...and setup available providers
   providers.forEach( function( provider ) {
      nodes.push( { x : 0, y : 0 } );
   } );

   d3.select( window ).on( "resize", resize );

   var radius; // set in the next select

   svg.select( "g" ).each( function() {
      radius = parseInt( d3.select( this ).select( "text" ).style( "width" ), 10 ) / 2;
   } );

   var force = d3.layout.force()
      .gravity( 0.075 )
      .distance( parseInt( wrapper.style( "width" ), 10 ) / providers.length )
      .charge( -1500 )
   ; // size()ed in resize()

   force
      .nodes( nodes )
   ; // start()ed in resize()

   function resize() {
      width = parseInt( wrapper.style( "width" ), 10 );
      height = parseInt( wrapper.style( "height" ), 10 );

      if ( height < 320 ) height = app.hackHeight( wrapper ); // HACK

      svg
         .attr( "width", width )
         .attr( "height", height )
      ;

      focusSelected = {
         x : width - radius,
         y : radius
      };
      focusDefault = {
         x : width / 2,
         y : height / 2
      };

      force
         .size( [ width, height ] )
         .start()
      ;
   }

   // init
   resize();

   var node = svg.selectAll( "g" ) // for the svg animation
      .data( nodes )
   ;

   var text = node.selectAll( "text" )
      .on( "click", function() {
         var parent = d3.select( this.parentNode );
         var d = parent.datum();

         if ( selected === d ) {
            d.selected = !d.selected;
         } else {
            if ( selected ) {
               delete( selected.selected );
            }

            selected = d;

            d.selected = true;
         }

         //setSelected( d.selected ? parent.attr( "provider" ) : null ); // callback into GWT

         force.start();
      } )
   ;

   force.on( "tick", function( e ) {
      // http://bl.ocks.org/mbostock/1021953
      var k = 0.1 * e.alpha;

      nodes.forEach( function( o ) {
         var focus = o.selected ? focusSelected : focusDefault;

         o.x += ( focus.x - o.x ) * k;
         o.y += ( focus.y - o.y ) * k;
      } );

      node
         .attr( "transform", function( d ) {
            return "translate(" + d.x + "," + d.y + ")";
         } )
      ;
   } );
}
