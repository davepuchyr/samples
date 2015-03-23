#!/usr/bin/perl -w
# $Id: db2json.pl 32 2009-12-18 21:32:15Z dave $

use strict;
use JSON::XS;
use Utils;

$/ = undef;

my $dbh = Utils::dbh();
my $sql = !defined( $ENV{REQUEST_METHOD} ) || $ENV{REQUEST_METHOD} eq 'GET' ? shift( @ARGV ) : <STDIN>;
my $sth = $dbh->prepare( $sql ) or die $dbh->errstr;

$sth->execute() or die $dbh->errstr;

my @rows;
while ( my $href = $sth->fetchrow_hashref() ) {
   push( @rows, $href );
}

$sth->finish() or die $dbh->errstr;
$dbh->disconnect() or die "couldn't disconnect: $!\n";

my $json = JSON::XS->new->utf8->encode( \@rows ) or die q|couldn't encode @rows to JSON|;

exit !print "Content-Type: text/plain\n\n$json\n";

