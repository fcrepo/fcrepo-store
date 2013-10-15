package com.github.cwilper.fcrepo.store.util.commands;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.cwilper.fcrepo.dto.core.ControlGroup;
import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.DatastreamVersion;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.core.FedoraStore;
import com.github.cwilper.fcrepo.store.core.FedoraStoreSession;
import com.github.cwilper.fcrepo.store.core.NotFoundException;
import com.github.cwilper.fcrepo.store.util.IdSpec;
import com.github.cwilper.fcrepo.store.util.filters.NonMutatingFilter;
import com.github.cwilper.fcrepo.store.util.filters.ds.SetFixity;


public class VerifyCopyCommand extends FilteringBatchObjectCommand {
    
    private final SetFixity m_fixityFilter;
    
    private final CommandContext m_destFactoryContext;
    
    public VerifyCopyCommand(FedoraStore source, FedoraStore destination,
            IdSpec pids, NonMutatingFilter<FedoraObject> filter,
            SetFixity fixityFilter) {
        this(source.getSession(), destination.getSession(),
                pids, filter, fixityFilter);
    }
        
    public VerifyCopyCommand(
            FedoraStoreSession source, FedoraStoreSession destination,
            IdSpec pids, NonMutatingFilter<FedoraObject> filter,
            SetFixity fixityFilter) {
        this(source, destination, pids, filter, fixityFilter,
                LoggerFactory.getLogger(VerifyCopyCommand.class));
    }

    public VerifyCopyCommand(
            FedoraStoreSession source, FedoraStoreSession destination,
            IdSpec pids, NonMutatingFilter<FedoraObject> filter,
            SetFixity fixityFilter, Logger logger) {
        super(source, destination, pids, filter, logger);
        m_fixityFilter = fixityFilter;
        m_destFactoryContext = CommandContext.nonModifiableContext(destination, destination, null);
    }

    @Override
    protected void handleFilteredObject(FedoraObject object) {
        FedoraObject destinationObject;
        try {
            destinationObject = factoryContext.getDestination().getObject(object.pid());
        } catch (NotFoundException e) {
            logger.error(object.pid() + " was not found in the destination store");
            return;
        }
        if (!object.pid().equals(destinationObject.pid())) {
            logger.error("Destination store loaded an object with the wrong PID (expected "
                    + object.pid() + " got " + destinationObject.pid() + ")");
            return;
        }
        SortedMap<String, Datastream> srcDatastreams =
                object.datastreams();
        SortedMap<String, Datastream> destDatastreams =
                destinationObject.datastreams();
        
        for (Entry<String, Datastream> entry: srcDatastreams.entrySet()) {
            Datastream left;
            Datastream right;
            if (!ControlGroup.MANAGED.equals(entry.getValue().controlGroup())
                    && !ControlGroup.INLINE_XML.equals(entry.getValue().controlGroup())) {
                continue;
            }
            try {
                left = m_fixityFilter.accept(entry.getValue(),
                        factoryContext.copyFor(object));
                right = destDatastreams.get(entry.getKey());
                if (right == null) {
                    logger.error(object.pid() + " " + entry.getKey() + " was not found in the destination store");
                    continue;
                }
                right = m_fixityFilter.accept(right,
                        m_destFactoryContext.copyFor(object));
            } catch (IOException e) {
                logger.error("Could not read source and/or destination datastream contents" +
                             " for " + object.pid() + " " + entry.getKey(), e);
                continue;
            }
            DatastreamVersion lastLeft = left.versions().last();
            DatastreamVersion lastRight = right.versions().last();

            if (lastRight.createdDate().compareTo(lastLeft.createdDate()) != 0) {
                logger.warn("Last creation dates do not match for source and destination "
                    + object.pid() + " " + left.id());
                for (DatastreamVersion version: right.versions()) {
                    if (version.createdDate().equals(lastLeft.createdDate())){
                        lastRight = version;
                    }
                }
            }
            
            long leftSize = lastLeft.size();
            long rightSize = lastRight.size();
            if ( leftSize != rightSize) {
                logger.error("Source size " + leftSize + " not equal to destination size " +
                             rightSize + " for " + object.pid());
                continue;
            }
            String leftDigest = lastLeft.contentDigest().hexValue();
            String rightDigest = lastRight.contentDigest().hexValue();
            if (!leftDigest.equals(rightDigest)) {
                logger.error("Source digest " + leftDigest + " not equal to destination digest " +
                        rightDigest + " for " + object.pid());
                continue;
            }
        }
    }

}
