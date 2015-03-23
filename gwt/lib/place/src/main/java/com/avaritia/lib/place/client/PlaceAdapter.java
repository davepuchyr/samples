// $Id$
package com.avaritia.lib.place.client;

import com.google.gwt.place.shared.Place;


abstract public class PlaceAdapter<V extends PlaceAdapterVisitor> extends Place implements PlaceAdapterAcceptor<V> {
}
