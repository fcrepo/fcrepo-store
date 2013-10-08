package com.github.cwilper.fcrepo.store.util.filters.ds;

import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.util.IdSpec;
import com.github.cwilper.fcrepo.store.util.filters.NonMutatingFilter;

/**
 * Accepts datastreams whose ids match a given {@link IdSpec}.
 */
public class IfIdMatches extends NonMutatingFilter<Datastream> {
    private final IdSpec ids;

    public IfIdMatches(IdSpec ids) {
        this.ids = ids;
    }

    @Override
    public boolean accepts(Datastream datastream, FedoraObject object) {
        return ids.matches(datastream.id());
    }
}
