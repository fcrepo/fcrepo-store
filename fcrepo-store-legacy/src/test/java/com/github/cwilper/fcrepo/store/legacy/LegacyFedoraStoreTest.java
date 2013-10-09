package com.github.cwilper.fcrepo.store.legacy;

import static org.mockito.Mockito.mock;

import com.github.cwilper.fcrepo.dto.core.io.DTOReader;
import com.github.cwilper.fcrepo.dto.core.io.DTOWriter;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLReader;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLWriter;
import com.github.cwilper.fcrepo.store.core.FedoraStore;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link LegacyFedoraStore}.
 */
public class LegacyFedoraStoreTest {
    private FileStore testObjectStore;
    private FileStore testContentStore;

    @Before
    public void setUp() {
        PathAlgorithm alg = new TimestampPathAlgorithm();
        testObjectStore = new MemoryFileStore(new MemoryPathRegistry(), alg);
        testContentStore = new MemoryFileStore(new MemoryPathRegistry(), alg);
    }

    @Test(expected=NullPointerException.class)
    public void initWithNullObjectStore() {
        new LegacyFedoraStore(null,
                mock(FileStore.class),
                mock(DTOReader.class),
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullContentStore() {
        new LegacyFedoraStore(mock(FileStore.class),
                null,
                mock(DTOReader.class),
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullReaderFactory() {
        new LegacyFedoraStore(mock(FileStore.class),
                mock(FileStore.class),
                null,
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullWriterFactory() {
        new LegacyFedoraStore(mock(FileStore.class),
                mock(FileStore.class),
                mock(DTOReader.class),
                null);
    }
    
    @Test
    public void getSession() {
        FedoraStore store = new LegacyFedoraStore(testObjectStore,
                testContentStore, new FOXMLReader(), new FOXMLWriter());
        store.getSession().close();
    }
}
