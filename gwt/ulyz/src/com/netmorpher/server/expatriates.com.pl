#!/usr/bin/perl
# $Id: expatriates.com.pl 103 2010-05-17 13:44:21Z dave $

use strict;
use CGI;
use ENV;
use HTTP::Request::Common qw( POST );
use LWP::UserAgent;
use JSON::XS;

$| = 1;

my $q = new CGI();
my $json = JSON::XS->new->ascii->allow_nonref; # NOTE: ascii as expected by nm
my %bunch;
my @trouble;

print "Content-Type: text/plain\r\n\r\nPublished:\n\n";

foreach my $key qw( place other title description email category subcategory ) {
   my $value = $q->param( $key );
   $value =~ s|^\s+||o;
   print "$key => $value\n";
   do { push( @trouble, $key ); next; } if ( $key ne 'other' && !length( $value ) );
   $bunch{$key} = $json->encode( $value ); # encode "s, etc
}

exit !print "\ncan't run morph due to missing MANDATORY fields: " . join( ', ', @trouble ) if ( scalar( @trouble ) );

my ( $category, $subcategory ) = ( $q->param( 'category' ), $q->param( 'subcategory' ) );
my $graph =<<EOS;
/domains/com/expatriates/PlaceSimpleAd.1
/domains/com/expatriates/PlaceSimpleAd.1.category=$bunch{category}
/domains/com/expatriates/PlaceSimpleAd.1.subcategory=$bunch{subcategory}
/domains/com/expatriates/PlaceSimpleAd.1.place=$bunch{place}
/domains/com/expatriates/PlaceSimpleAd.1.other=$bunch{other}
/domains/com/expatriates/PlaceSimpleAd.1.title=$bunch{title}
/domains/com/expatriates/PlaceSimpleAd.1.description=$bunch{description}
/domains/com/expatriates/PlaceSimpleAd.1.email=$bunch{email}
EOS

print "\nMorph Input:\n\n$graph\n\nMorph Output (may take a while...):\n\n";

$graph =~ s/([^A-Za-z0-9])/sprintf("%%%02X", ord($1))/seg;

my $req = POST "http://$ENV{NM_HOST}:$ENV{NM_PORT}/", [ h => 'j', a => 'g', j => $graph ];
my $ua = LWP::UserAgent->new;
print $ua->request( $req )->decoded_content;

