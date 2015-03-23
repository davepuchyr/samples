// $Id$
package com.avaritia.app.zingers.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.avaritia.app.zingers.client.PlaceHistoryMapper;
import com.avaritia.app.zingers.server.domain.Category;
import com.avaritia.app.zingers.server.domain.Zinger;
import com.avaritia.app.zingers.server.service.CategoryService;
import com.avaritia.app.zingers.server.service.ZingerService;
import com.avaritia.lib.server.domain.User;
import com.avaritia.lib.server.service.TemplateService;
import com.avaritia.lib.server.service.UserService;
import com.google.common.base.Joiner;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.googlecode.objectify.NotFoundException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.TemplateException;

/**
 * @see <a href=https://developers.facebook.com/docs/opengraph/using-objects/#selfhosted>this</a>.
 */
@Singleton
public class ZingerServlet extends HttpServlet {
   //final static private Logger LOGGER = LoggerFactory.getLogger( ZingerServlet.class );

   final static private long serialVersionUID = -2865486723590678719L;

   @Inject @Named( "com.avaritia.lib.provider.client.Facebook.appId" )       private String appId;
   @Inject @Named( "com.avaritia.app.zingers.server.ZingerServlet.docRoot" ) private String docRoot;
   @Inject @Named( "com.avaritia.app.zingers.server.ZingerServlet.ogImage" ) private String ogImage;
   @Inject @Named( "com.avaritia.app.zingers.server.ZingerServlet.ogType" )  private String ogType;

   // inject services to make sure that static members are initialized even if we only use static methods
   @Inject CategoryService categoryService;
   @Inject TemplateService templateService;
   @Inject ZingerService zingerService;

   static protected final List<Category> listCategorys = CategoryService.getCategorys();

   static protected final TemplateLoader templateLoader = new ClassTemplateLoader( ZingerServlet.class, "template" ); // HARD-CODED


   @Override public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException {
      try {
         String[] params = request.getParameter( "user.zinger" ).split( "\\." ); // HARD-CODED in conjunction with zingers.js
         String suser = params[0];
         String sid = params[1];
         User user = UserService.find( suser );
         Zinger zinger = ZingerService.find( user, sid );
         List<String> categorys = new ArrayList<String>();
         Long mask = zinger.getCategorysMask();

         for ( int i = 0, n = listCategorys.size(); i < n && mask > 0; ++i ) {
            Long maski = listCategorys.get( i ).getMask();

            if ( ( mask & maski ) == maski ) {
               mask -= maski;

               categorys.add( listCategorys.get( i ).getName() );
            }
         }

         Map<String, Object> input = new HashMap<String, Object>();

         input.put( "PREFIX_ZINGERS", PlaceHistoryMapper.PREFIX_ZINGERS );
         input.put( "appId", appId );
         input.put( "category", zinger.getCategorysMask() );
         input.put( "categorys", Joiner.on( "/" ).join( categorys ) );
         input.put( "docRoot", docRoot );
         input.put( "ogImage", ogImage );
         input.put( "ogType", ogType );
         input.put( "url", request.getRequestURL().append( "?user.zinger=" ).append( suser ).append( "." ).append( sid ) );
         input.put( "value", zinger.getValue() );

         try {
            response.setContentType( "text/html; charset=UTF-8" );

            String html = templateService.toString( templateLoader, "Zinger.html", input );
            PrintWriter out = response.getWriter();

            out.print( html );
            out.close();
         } catch ( TemplateException e ) {
            e.printStackTrace();
         }
      } catch ( NumberFormatException e ) {
         response.sendError( HttpServletResponse.SC_NO_CONTENT );
      } catch ( NotFoundException nfe ) {
         response.sendError( HttpServletResponse.SC_NO_CONTENT );
      }
   }
}
