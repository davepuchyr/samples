/**
 * 
 */
package com.buyside.db;

import java.util.List;
import java.util.Map;

/**
 * @author dave
 * 
 */
public interface EntityHelperService {
   public interface EntityDefinition {
      public String getTable();
      
      public List<String> getColumns();
      
      public Map<String, String> getColumnSynonyms();
   }


   public EntityDefinition getEntityDefinition( Class<?> clazz );
}
