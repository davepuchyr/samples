// $Id$
package com.avaritia.app.zingers.shared;

import java.util.List;
import com.google.appengine.api.datastore.Cursor;


public class Page<T> {
   Cursor cursor;

   List<T> list;


   public Page() { // default constructor to avoid warning from RequestFactory
      throw new RuntimeException( "No arg Page constructor called - that should never happen!" );
   }


   public Page( Cursor cursor, List<T> list ) {
      this.cursor = cursor;
      this.list = list;
   }


   public String getCursor() {
      return cursor.toWebSafeString();
   }


   public List<T> getList() {
      return list;
   }
}
