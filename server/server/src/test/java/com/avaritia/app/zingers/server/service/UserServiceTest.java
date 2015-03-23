// $Id$
package com.avaritia.app.zingers.server.service;

import static org.junit.Assert.assertTrue;
import java.util.List;
import org.junit.Test;
import com.avaritia.lib.server.domain.User;
import com.avaritia.lib.server.service.UserService;


public class UserServiceTest extends OfyServiceTest {
   @Test public void getUsers() {
      List<User> users = UserService.getUsers();

      assertTrue( users != null );
      assertTrue( users.size() > 0 );
   }


   @Test public void findByMail() {
      User user = UserService.findByMail( "dave.puchyr@avaritia.com" );

      assertTrue( user != null );
      assertTrue( user.getId() == 1L );
   }


   @Test public void findByPhone() {
      User user = UserService.findByPhone( "+14038138563" );

      assertTrue( user != null );
      assertTrue( user.getId() == 2L );
   }
}
