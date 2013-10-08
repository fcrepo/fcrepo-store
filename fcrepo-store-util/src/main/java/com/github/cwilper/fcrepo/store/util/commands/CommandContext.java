package com.github.cwilper.fcrepo.store.util.commands;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.core.FedoraStoreSession;

/**
 * Provides {@link ThreadLocal} data relevant to the currently-executing
 * {@link Command}.
 */
public class CommandContext {
    private FedoraStoreSession tSource;

    private FedoraStoreSession tDestination;

    private FedoraObject tObject;
    
    public CommandContext(FedoraStoreSession source,
            FedoraStoreSession destination, FedoraObject object) {
        tSource = source;
        tDestination = destination;
        tObject = object;
    }

    public void setSource(FedoraStoreSession source) {
        tSource = source;
    }

    public FedoraStoreSession getSource() {
        return tSource;
    }

    public void setDestination(FedoraStoreSession destination) {
        tDestination = destination;
    }

    public FedoraStoreSession getDestination() {
        return tDestination;
    }

    public void setObject(FedoraObject object) {
        tObject = object;
    }

    public FedoraObject getObject() {
        return tObject;
    }
    
    public CommandContext copyFor(FedoraObject object) {
        return new CommandContext(tSource, tDestination, object);
    }
    
    public static CommandContext nonModifiableContext(
            FedoraStoreSession source, FedoraStoreSession destination,
            FedoraObject object) {
        return new CommandContext(source, destination, object) {
            @Override
            public void setSource(FedoraStoreSession object) {
                throw new UnsupportedOperationException("Cannot modify this CommandContext");
            }
            @Override
            public void setDestination(FedoraStoreSession object) {
                throw new UnsupportedOperationException("Cannot modify this CommandContext");
            }
            @Override
            public void setObject(FedoraObject object) {
                throw new UnsupportedOperationException("Cannot modify this CommandContext");
            }
        };
    }
}
