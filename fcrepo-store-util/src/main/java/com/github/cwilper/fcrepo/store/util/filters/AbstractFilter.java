package com.github.cwilper.fcrepo.store.util.filters;

import com.github.cwilper.ttff.Closeable;

/**
 * Convenience base class for {@link Filter} implementations.
 * <p>
 * Implements {@link #close()} as a no-op, which subclasses may override.
 *
 * @param <T> the type over which the filter operates.
 */
public abstract class AbstractFilter<T>
        implements Filter<T>, Closeable {

    /** Constructor for use by subclasses. */
    protected AbstractFilter() { }

    /**
     * Doesn't do anything by default; subclasses may override.
     */
    @Override
    public void close() {
        // no-op
    }
}
