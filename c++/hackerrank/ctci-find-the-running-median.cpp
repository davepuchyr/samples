// https://www.hackerrank.com/challenges/ctci-find-the-running-median
#include <iomanip>
#include <iostream>
#include <set>

using namespace std;


int main() {
   int i, n;
   multiset<int> a;
 
   cout << fixed << setprecision( 1 );
   cin >> n;

   for ( int a_i = 0; a_i < n; ++a_i ) {
      cin >> i;
      a.insert( i );

      auto it = a.begin();
      auto m = a_i / 2;
      
      advance( it, m );

      float median = *it;

      if ( a_i % 2 ) {
         advance( it, 1 );
         median = 0.5 * ( median + *it );
      }

      cout << median << endl;
   }

   return 0;
}
