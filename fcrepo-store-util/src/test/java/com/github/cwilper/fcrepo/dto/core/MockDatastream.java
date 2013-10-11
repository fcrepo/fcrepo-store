package com.github.cwilper.fcrepo.dto.core;

import java.util.Date;


/**
 * Test scaffolding to work around unmockable final methods
 * @author armintor@gmail.com
 *
 */
public class MockDatastream extends Datastream {

    public MockDatastream(String id) {
        super(id);
    }
    
    public void versions(DatastreamVersion... versions) {
        this.versions().clear();
        for (DatastreamVersion version: versions) {
            this.versions().add(version);
        }
    }
    
    public void newVersion(Date date, byte[] content) {
        
    }

}
