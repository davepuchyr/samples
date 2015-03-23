// $Id$
package com.avaritia.lib.injector.shared;

import java.io.InputStream;
import com.google.gwt.inject.client.binder.GinBinder;

/**
 * Reads constants from an InputStream into a Properties object that are subsequently bound to the binder's module; in the shared
 * package so that client and server can share the properties file.
 *
 * @param <T>
 *           either a {@link GinBinder} on the client side or a {@link Binder} on the server side.
 */
public interface ConstantsStreamBinder<T> {
   public void bind( T binder, InputStream is );
}
