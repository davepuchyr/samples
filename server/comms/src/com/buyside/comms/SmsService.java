// $Id$
package com.buyside.comms;

import com.buyside.common.BuySideBroadcaster;
import com.buyside.common.BuySideBroadcasterVisitor;

/**
 * @author dave
 *
 */
public abstract class SmsService implements BuySideBroadcaster {
   public void accept( BuySideBroadcasterVisitor visitor, Object o ) {
      visitor.visit( this, o );
   }
}
