// https://www.hackerrank.com/challenges/ctci-find-the-running-median
#include <iomanip>
#include <iostream>
#include <set>

using namespace std;


int main() {
   multiset<int> hi;
   multiset<int, greater<int>> lo;

   cout << fixed << setprecision( 1 );

   auto print = [&hi, &lo]( int i ) {
      float median = *hi.begin();

      if ( i & 1 ) median = 0.5 * ( median + *lo.begin() );

      cout << median << endl;
   };

   auto hi2lo = [&hi, &lo]() {
      auto it = hi.begin();
      lo.insert( *it );
      hi.erase( it );
   };

   auto lo2hi = [&hi, &lo]() {
      auto it = lo.begin();
      hi.insert( *it );
      lo.erase( it );
   };

   int x, n, i = 0;

   cin >> n;

   if ( n < 1 ) return 0;

   cin >> x;

   hi.insert( x );

   print( i++ );

   if ( n < 2 ) return 0;

   cin >> x;

   if ( x < *hi.begin() ) {
      lo.insert( x );
   } else {
      hi2lo();
      hi.insert( x );
   }

   print( i++ );

   while ( i < n ) {
      cin >> x;

      auto nhi = hi.size(), nlo = lo.size();
      auto hi0 = *hi.begin(), lo0 = *lo.begin();

      if ( x < lo0 ) {
         if ( nlo == nhi ) lo2hi();

         lo.insert( x );
      } else if ( x > hi0 ) {
         if ( nhi > nlo ) hi2lo();

         hi.insert( x );
      } else {
         if ( nlo < nhi ) {
            lo.insert( x );
         } else {
            hi.insert( x );
         }
      }

      print( i++ );
   }

   return 0;
}
