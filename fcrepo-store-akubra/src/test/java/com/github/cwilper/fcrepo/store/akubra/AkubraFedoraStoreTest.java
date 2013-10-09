package com.github.cwilper.fcrepo.store.akubra;

import static org.mockito.Mockito.mock;

import com.github.cwilper.fcrepo.dto.core.io.DTOReader;
import com.github.cwilper.fcrepo.dto.core.io.DTOWriter;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLReader;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLWriter;
import com.github.cwilper.fcrepo.store.core.FedoraStore;
import org.akubraproject.BlobStore;
import org.akubraproject.mem.MemBlobStore;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

/**
 * Unit tests for {@link AkubraFedoraStore}.
 */
public class AkubraFedoraStoreTest {
    private BlobStore testObjectStore;
    private BlobStore testContentStore;

    @Before
    public void setUp() {
        testObjectStore = new MemBlobStore(URI.create("urn:objects"));
        testContentStore = new MemBlobStore(URI.create("urn:content"));
    }

    @Test(expected=NullPointerException.class)
    public void initWithNullObjectStore() {
        new AkubraFedoraStore(null,
                mock(BlobStore.class),
                mock(DTOReader.class),
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullContentStore() {
        new AkubraFedoraStore(mock(BlobStore.class),
                null,
                mock(DTOReader.class),
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullReaderFactory() {
        new AkubraFedoraStore(mock(BlobStore.class),
                mock(BlobStore.class),
                null,
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullWriterFactory() {
        new AkubraFedoraStore(mock(BlobStore.class),
                mock(BlobStore.class),
                mock(DTOReader.class),
                null);
    }
    
    @Test
    public void getSession() {
        FedoraStore store = new AkubraFedoraStore(testObjectStore,
                testContentStore, new FOXMLReader(), new FOXMLWriter());
        store.getSession().close();
    }
}
