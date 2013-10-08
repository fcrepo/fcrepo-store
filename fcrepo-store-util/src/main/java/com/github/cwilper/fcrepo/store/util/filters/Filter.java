package com.github.cwilper.fcrepo.store.util.filters;

import java.io.IOException;

import com.github.cwilper.fcrepo.store.util.commands.CommandContext;
import com.github.cwilper.ttff.Closeable;

/**
 * A function that accepts, rejects, or transforms objects.
 *
 * @param <T> the type over which the filter operates.
 */
public interface Filter<T> extends Closeable {

    /**
     * Accepts, rejects, or transforms the given object.
     *
     * @param item the object.
     * @return The original object, a derivative, or <code>null</code> if
     *         the object is rejected.
     * @throws IOException if an I/O problem occurs.
     */
    T accept(T item, CommandContext context) throws IOException;

}