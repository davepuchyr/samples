// $Id: Base2DerivedWithoutRTTI.cpp 5 2009-08-10 21:41:35Z dave $

// Date: Mon, Jul 6, 2009 at 7:15 PM
// Subject: C++ question
// From: "Peter J. Puchyr"
// To: Dave Puchyr, Radim Gmail
//
// Hi Dave, Radim,
//
// Would you please give me some C++ advice if you can?  The attached pdf file
// is 2 pages long, but the question is pretty fundamental.
//
// Given that I need to store a collection of objects with multiple
// inheritance, I create a vector of pointers to a particular base class.  But
// on retrieving the pointer, I need to refer to methods which are not part of
// the particular base class used to save the pointer.  Is there a standard
// way, short of doing a dynamic cast, of referring to common methods which
// are not part of the particular base class?
//
// The attached pdf file may make the question more clear.
//
// Peter

// Date: Tue, 7 Jul 2009 07:42:23 -0500
// Subject: C++ question
// From: "Peter J. Puchyr"
// To: Dave Puchyr
//
// Hey Dave,
//
// Wow, that was amazing!  First that you understood what I was trying to get
// at, and second that you provided a complete, working example!  Now I'm
// trying to wrap my head around why the example works.  The essense seems to
// be that the runtime knows the actual (derived) type of an argument even
// when it is declared as a base class.
//
// Thanks, Dave!
// Dad

#include <iostream>

// forward declarations of derived classes
class B;
class C;

// interfaces
class Visitor {
public:
   virtual void visit(B* b) = 0;
   virtual void visit(C* c) = 0;
};


class Acceptor {
public:
   virtual void accept(Visitor* v) = 0;
};


// base
class A : public Visitor, public Acceptor {
public:
   A() {
   }

   virtual ~A() {
   }

   virtual void accept(Visitor* v) = 0;

   void visit(B* b); // impl below
   void visit(C* c); // impl below
};


// derived
class B : public A {
public:
   B() {
   }

   void accept(Visitor* a) {
      a->visit(this);
   }

   void g() {
      std::cout << "in B::g()\n";
   }
};


// derived
class C : public A {
public:
   C() {
   }

   void accept(Visitor* a) {
      a->visit(this);
   }

   void h() {
      std::cout << "in C::h()\n";
   }
};


// A's impl
void A::visit(B* b) {
   b->g();
}


void A::visit(C* c) {
   c->h();
}


int main(int argc, char* argv[]) {
   A* a[] = { new B(), new C() };

   for ( int i = 0, n = sizeof(a) / sizeof(A*); i < n; ++i ) {
      a[i]->accept(a[i]);
      delete a[i];
   }

   return 0;
}

