// $Id$
package com.avaritia.lib.place.client;

import com.google.gwt.activity.shared.Activity;


public interface PlaceAdapterAcceptor<V extends PlaceAdapterVisitor> {
   public Activity accept( V visitor );
}
