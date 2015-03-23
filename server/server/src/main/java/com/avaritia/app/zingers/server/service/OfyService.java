// $Id$
package com.avaritia.app.zingers.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;
import com.avaritia.app.zingers.server.domain.Favorite;
import com.avaritia.app.zingers.server.domain.Rating;
import com.avaritia.app.zingers.server.domain.Zingable;
import com.avaritia.app.zingers.server.domain.ZingableCSV;
import com.avaritia.app.zingers.server.domain.Zinger;
import com.avaritia.app.zingers.server.domain.ZingerCSV;
import com.avaritia.lib.server.domain.DatastoreObject;
import com.avaritia.lib.server.domain.Provider;
import com.avaritia.lib.server.domain.User;
import com.avaritia.lib.server.service.ProviderService;
import com.googlecode.objectify.Key;

/**
 * Bootstraps the datastore when necessary.
 *
 * @see <a href=https://code.google.com/p/objectify-appengine/wiki/BestPractices>Objectify Best Practices</a>
 */
public class OfyService extends com.avaritia.lib.server.service.OfyService {
   static private final Logger LOGGER = LoggerFactory.getLogger( OfyService.class );

   static private final Class<?>[] ENTITIES = new Class<?>[] {
        Favorite.class
      , Provider.class
      , Zinger.class
      , Rating.class
      , User.class
      , Zingable.class
   };

   @Override public Class<?>[] getEntities() {
      return ENTITIES;
   }


   @Override protected void bootstrap() {
      LOGGER.info( "bootstrap(): entered" );

      long t0 = System.currentTimeMillis();
      Optional f = new Optional( new ParseDouble() );
      Optional l = new Optional( new ParseLong() );
      BootstrapEntity[] entities = new BootstrapEntity[] { // order is important
            new BootstrapEntity( "/com/avaritia/lib/server/domain/PROVIDERS.csv", Provider.class, new CellProcessor[] { l, null } )
          , new BootstrapEntity( "/com/avaritia/lib/server/domain/USERS.csv", User.class, new CellProcessor[] { l, null, null, null, null, null, null, null } )
          , new BootstrapEntity( "/com/avaritia/app/zingers/server/domain/ZINGERS.csv", Zinger.class, new CellProcessor[] { l, l, null, null, l, l, f, null } )
          , new BootstrapEntity( "/com/avaritia/app/zingers/server/domain/ZINGABLES.csv", Zingable.class, new CellProcessor[] { l, null, null } )
       };

      for ( BootstrapEntity be : entities ) {
         if ( ofy().load().type( be.clazz ).first().now() == null ) {
            try {
               if ( be.clazz.equals( Zinger.class ) ) {
                  populateFromZingerCSV( be.filename, be.cellProcessor );
               } else if ( be.clazz.equals( Zingable.class ) ) {
                  populateFromZingableCSV( be.filename, be.cellProcessor );
               } else {
                  populateFromCSV( be.filename, be.clazz, be.cellProcessor );
               }
            } catch ( IOException e ) {
               LOGGER.error( "bootstrap(): failed to bootstrap {}", be.filename );
            }
         }
      }

      LOGGER.info( "bootstrap(): exiting; dt == {}ms;", System.currentTimeMillis() - t0 );
   }


   private void populateFromZingableCSV( String file, CellProcessor[] processors ) throws IOException {
      LOGGER.warn( "populateFromZingableCSV(): bootstrapping {}", file );

      long t0 = System.currentTimeMillis();
      InputStream is = User.class.getResourceAsStream( file );
      InputStreamReader isr = new InputStreamReader( is );
      ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );

      try {
         Object o;
         final String[] header = reader.getHeader( true );
         ArrayList<DatastoreObject> dsos = new ArrayList<DatastoreObject>();

         while ( ( o = reader.read( ZingableCSV.class, header, processors ) ) != null ) {
            ZingableCSV csv = (ZingableCSV) o;
            Provider provider = ProviderService.findByNameNotNull( csv.getProvider() );
            Key<Provider> kprovider = Key.create( Provider.class, provider.getId() );
            Zingable zingable = new Zingable( kprovider, csv );

            dsos.add( zingable );
         }

         ofy().save().entities( dsos ).now();

         LOGGER.warn( "populateFromZingableCSV():  bootstrapped {} in {}ms", file, System.currentTimeMillis() - t0 );
      } finally {
         reader.close();
      }
   }


   /**
    * Populates {@link Zinger}s, {@link Favorite}s, and {@link Rating}s.
    */
   private void populateFromZingerCSV( String file, CellProcessor[] processors ) throws IOException {
      LOGGER.warn( "populateFromZingerCSV(): bootstrapping {}", file );

      long t0 = new Date().getTime();
      InputStream is = User.class.getResourceAsStream( file );
      InputStreamReader isr = new InputStreamReader( is );
      ICsvBeanReader reader = new CsvBeanReader( isr, CsvPreference.EXCEL_PREFERENCE );

      try {
         Object o;
         final String[] header = reader.getHeader( true );
         ArrayList<DatastoreObject> dsos = new ArrayList<DatastoreObject>();
         int i = 1;

         while ( ( o = reader.read( ZingerCSV.class, header, processors ) ) != null ) {
            ZingerCSV csv = (ZingerCSV) o;
            Long uid = csv.getUser();
            ++i;

            if ( ofy().load().type( User.class ).id( uid ).now() == null ) { // hits the cache, so not expensive
               LOGGER.error( "populateFromZingerCSV(): unexpected user id {} at line {}", uid, i );
            }

            Key<User> kuser = Key.create( User.class, csv.getUser() );
            Zinger zinger = new Zinger( kuser, csv );
            Key<Zinger> kzinger = Key.create( Zinger.class, zinger.getId() );

            for ( String name : csv.getCategorys() ) {
               Long mask = CategoryService.findMaskByName( name.toUpperCase().replace( " ", "_" ) ); // HARD-CODED

               try {
                  zinger.addCategoryMask( mask );
               } catch ( NullPointerException e ) {
                  LOGGER.error( "populateFromZingerCSV(): NPE at line {}", i );
                  throw e;
               }
            }

            dsos.add( zinger );

            // favorite
            uid = csv.getFavoriteUser();

            if ( uid != null && ofy().load().type( User.class ).id( uid ).now() == null ) { // hits the cache, so not expensive
               LOGGER.error( "populateFromZingerCSV(): unexpected favorite user id {} at line {}", uid, i );
            }

            if ( uid != null ) {
               Key<User> kfavoriteUser = Key.create( User.class, uid );
               Favorite favorite = new Favorite( kzinger, kfavoriteUser );

               dsos.add( favorite );
            }

            // rating
            uid = csv.getRatingUser();

            if ( uid != null && ofy().load().type( User.class ).id( uid ).now() == null ) { // hits the cache, so not expensive
               LOGGER.error( "populateFromZingerCSV(): unexpected rating user id {} at line {}", uid, i );
            }

            if ( uid != null ) {
               Key<User> kratingUser = Key.create( User.class, uid );
               Double value = csv.getRatingValue();
               Rating rating = new Rating( kzinger, kratingUser, value );

               dsos.add( rating );
            }
         }

         ofy().save().entities( dsos ).now();

         LOGGER.warn( "populateFromZingerCSV():  bootstrapped {} in {}ms", file, System.currentTimeMillis() - t0 );
      } finally {
         reader.close();
      }
   }
}
