package com.github.cwilper.fcrepo.store.util.filters;

import java.io.IOException;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.util.commands.CommandContext;

public abstract class NonMutatingFilter<T>
        extends AbstractFilter<T> {

    @Override
    public final T accept(T item, CommandContext context) throws IOException {
        if (accepts(item, context.getObject())) {
            return item;
        } else {
            return null;
        }
    }

    /**
     * Object may be redundant for FedoraObject filters, but is necessary for
     * Datastream filters
     * @param item
     * @param object - the FedoraObject to which item belongs
     * @return
     * @throws IOException
     */
    protected abstract boolean accepts(T item, FedoraObject object) throws IOException;
}