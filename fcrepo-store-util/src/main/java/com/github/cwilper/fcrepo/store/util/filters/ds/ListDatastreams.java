package com.github.cwilper.fcrepo.store.util.filters.ds;

import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.DatastreamVersion;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.core.StoreException;
import com.github.cwilper.fcrepo.store.util.filters.NonMutatingFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Lists each datastream.
 */
public class ListDatastreams extends NonMutatingFilter<Datastream> {
    private static final Logger logger =
            LoggerFactory.getLogger(ListDatastreams.class);

    private final boolean allDatastreamVersions;
    private final boolean includeVersions;

    public ListDatastreams(boolean includeVersions,
            boolean allDatastreamVersions) {
        this.allDatastreamVersions = allDatastreamVersions;
        this.includeVersions = includeVersions;
    }

    @Override
    public boolean accepts(Datastream ds, FedoraObject object) throws IOException {
        String info = object.pid() + "/" + ds.id();
        if (includeVersions) {
            try {
                if (allDatastreamVersions) {
                    for (DatastreamVersion datastreamVersion : ds.versions()) {
                        handleVersion(object, ds, datastreamVersion);
                    }
                } else {
                    handleVersion(object, ds, ds.versions().first());
                }
            } catch (StoreException e) {
                throw new IOException(e);
            }
        }
        logger.info(info);
        logger.debug(ds.toString());
        return true;
    }

    protected void handleVersion(FedoraObject object, Datastream ds,
            DatastreamVersion dsv) {
        String info = object.pid() + "/" + ds.id() + "/" + dsv.id();
        logger.info(info);
        logger.debug(dsv.toString());
    }

}
