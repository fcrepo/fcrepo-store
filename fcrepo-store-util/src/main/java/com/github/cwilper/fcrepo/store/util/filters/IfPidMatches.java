package com.github.cwilper.fcrepo.store.util.filters;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.util.IdSpec;

import java.io.IOException;

/**
 * Accepts objects whose pids match a given {@link IdSpec}.
 */
public class IfPidMatches extends NonMutatingFilter<FedoraObject> {
    private final IdSpec pids;

    public IfPidMatches(IdSpec pids) {
        this.pids = pids;
    }

    @Override
    public boolean accepts(FedoraObject object, FedoraObject duplicate) throws IOException {
        return pids.matches(object.pid());
    }
}
