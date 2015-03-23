/**
 * 
 */
package com.buyside.db;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author dave
 * 
 */
public class EntityHelperServiceImpl implements EntityHelperService {
   public EntityHelperServiceImpl() {
   }


   @Override public EntityDefinition getEntityDefinition( final Class<?> clazz ) {
      return new EntityDefinition() {
         @Override public String getTable() {
            return clazz.getAnnotation( Table.class ).name();
         }

         
         @Override public List<String> getColumns() {
            ArrayList<String> list = new ArrayList<String>();

            for ( Method method : clazz.getMethods() ) {
               Column column = method.getAnnotation( Column.class );
               
               if ( column == null ) continue;
               
               list.add( column.name() );
            }
            
            return list;
         }


         @Override public Map<String, String> getColumnSynonyms() {
            HashMap<String, String> map = new HashMap<String, String>();

            for ( Method method : clazz.getMethods() ) {
               Column column = method.getAnnotation( Column.class );
               
               if ( column == null ) continue;
               
               String name = column.name();
               String synonym = method.getName();
               
               if ( synonym.startsWith( "get" ) ) {
                  synonym = synonym.substring( 3 ).toUpperCase();
               } else {
                  synonym = null;
               }

               map.put( name, name );
               
               if ( name.indexOf( "_" ) != -1 ) { // remove underscores
                  map.put( name.replace( "_", "" ).toUpperCase(), name );
               }
               
               if ( !map.containsKey( synonym ) ) {
                  map.put( synonym, name );
               }
            }
            
            return map;
         }
      };
   }
}
