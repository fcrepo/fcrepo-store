package com.github.cwilper.fcrepo.dto.core;

import java.util.Date;


public class MockDatastreamVersion extends DatastreamVersion {

    public MockDatastreamVersion(String id, Date createdDate) {
        super(id, createdDate);
    }
    
    public MockDatastreamVersion contentMetadata(byte[] content) {
        size((long)content.length);
        ContentDigest digest = new ContentDigest();
        digest.hexValue(
                com.github.cwilper.fcrepo.store.util.filters.ds.Util.hexString(content));
        contentDigest(digest);
        return this;
    }

}
