package com.github.cwilper.fcrepo.store.util.commands;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.core.FedoraStoreSession;
import com.github.cwilper.fcrepo.store.util.IdSpec;
import com.github.cwilper.fcrepo.store.util.filters.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Base class for {@link BatchObjectCommand}s that also perform filtering.
 */
public abstract class FilteringBatchObjectCommand
        extends BatchObjectCommand {
    private static final Logger logger =
            LoggerFactory.getLogger(FilteringBatchObjectCommand.class);
    protected final Filter<FedoraObject> filter;

    protected final CommandContext factoryContext;
    
    public FilteringBatchObjectCommand(FedoraStoreSession source,
            IdSpec pids, Filter<FedoraObject> filter) {
        this(source, null, pids, filter);
    }
    
    protected FilteringBatchObjectCommand(
            FedoraStoreSession source, FedoraStoreSession destination,
            IdSpec pids, Filter<FedoraObject> filter) {
        super(source, pids);
        this.filter = filter;
        this.factoryContext =
                CommandContext.nonModifiableContext(source, destination, null);
    }

    @Override
    public void handleObject(FedoraObject object) {
        String pid = object.pid();
        try {
            object = filter.accept(object, factoryContext.copyFor(object));
            if (object == null) {
                logger.debug("Skipped {} (filtered out)", pid);
            } else {
                handleFilteredObject(object);
            }
        } catch (IOException e) {
            logger.warn("Skipped {} (error filtering)" + pid, e);
        }
    }

    protected abstract void handleFilteredObject(FedoraObject object);
}
