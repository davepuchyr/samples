package Utils;
# $Id: Utils.pm 30 2009-12-16 13:59:08Z dave $

use DBI;
use ENV;

sub dbh {
   my $href = shift;
   my $drh = DBI->install_driver( 'mysql' );
   my $dsn = "DBI:mysql:database=$ENV{DB_ULYZ};host=$ENV{DB_ULYZ_HOST};port=$ENV{DB_ULYZ_PORT}";

   $href = () if ( !defined( $href ) );
   $href->{mysql_enable_utf8} = 1 if ( !exists( $href->{mysql_enable_utf8} ) );
   $href->{InactiveDestroy} = 1 if ( !exists( $href->{InactiveDestroy} ) ); # disable auto desctruct of the handle on DESTROY

   return DBI->connect( $dsn, $ENV{DB_ULYZ_USER}, $ENV{DB_ULYZ_PASSWORD}, $href ) or die "$DBI::errstr\n";
}

1;

