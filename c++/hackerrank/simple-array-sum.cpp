// https://www.hackerrank.com/challenges/simple-array-sum
#include <fstream>
#include <iostream>

using namespace std;


int run( istream& is, ostream& os ) {
   string line;

   getline( is, line );

   int expect = atoi( &line[0] ), have = 0, sum = 0; // c++11+ is guaranteed to have contiguous data

   getline( is, line );

   char* p0 = &line[0];

   while ( *p0 && have < expect ) { // use have and expect given the instructions, ie they're not needed since line is \0 terminated
      char* p = p0 + 1;

      while ( *p && *p != ' ' ) ++p;

      *p = 0;

      sum += atoi( p0 );
      ++have;
      p0 = p + 1;
   }

   os << sum << endl;

   return 0;
}


int main( int argc, char** argv ) {
   istream* pis = argc < 2 ? &cin  : ( [argv]() { static ifstream s; s.open( argv[1] ); return &s; } )();
   ostream* pos = argc < 3 ? &cout : ( [argv]() { static ofstream s; s.open( argv[2] ); return &s; } )();

   return run( *pis, *pos );
}
