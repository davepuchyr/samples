#!/usr/bin/perl -w
# $Id: json2db.pl 34 2009-12-19 18:42:32Z dave $

use strict;
use JSON::XS;
use Utils;

$/ = undef;

my $json = !defined( $ENV{REQUEST_METHOD} ) || $ENV{REQUEST_METHOD} eq 'GET' ? shift( @ARGV ) : <STDIN>;
my $href = JSON::XS->new->utf8->decode( $json ) or die qq|couldn't decode '$json' to a perl hash|;
my $action  = $href->{action}  or die qq|couldn't find 'action' in '$json'|;
my $table   = $href->{table}   or die qq|couldn't find 'table' in '$json'|;

if ( $action =~ m|^delete$|i ) {
   my $dbh = Utils::dbh();
   my $sql = "DELETE FROM $table ";
   $sql   .= "WHERE $href->{clause}" if ( $href->{clause} );
   
   $dbh->do( $sql ) or die $dbh->errstr;
   $dbh->disconnect() or die "couldn't disconnect: $!\n";
   exit !print( "Content-Type: text/plain\n\n$sql\n" );
}

my $columns = $href->{columns} or die qq|couldn't find 'columns' in '$json'|;
my $values  = $href->{values}  or die qq|couldn't find 'values' in '$json'|;

die qq|columns and values must be of the same length in '$json'| if ( scalar( @{ $columns } ) != scalar( @{ $values } ) );

my $sql = undef;

if ( $action =~ m|^insert$|i ) {
   my $binding = join( ', ', map { '?' } @{ $columns } );
   $columns = '`' . join( '`, `' , @{ $columns } ) . '`';
   $sql = "INSERT INTO $table ( $columns ) VALUES ( $binding );";
} elsif ( $action =~ m|^update$|i ) {
   my $binding = join( ', ', map { "`$_` = ?" } @{ $columns } );
   $sql  = "UPDATE $table SET $binding ";
   $sql .= "WHERE $href->{clause}" if ( $href->{clause} );
} else {
   die qq|action must be one of 'insert', 'update', or 'delete' in '$json'|;
}

my $dbh = Utils::dbh();
my $sth = $dbh->prepare( $sql ) or die $dbh->errstr;

$sth->execute( @{ $values } ) or die $dbh->errstr;
$sth->finish() or die $dbh->errstr;
$dbh->disconnect() or die "couldn't disconnect: $!\n";

$values = join( ', ', @{ $values } );

exit !print( "Content-Type: text/plain\n\n$sql\nvalues = $values\n" );

