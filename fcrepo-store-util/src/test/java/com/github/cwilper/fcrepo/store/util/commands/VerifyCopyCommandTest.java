package com.github.cwilper.fcrepo.store.util.commands;

import static org.mockito.Mockito.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.dto.core.MockDatastream;
import com.github.cwilper.fcrepo.dto.core.MockDatastreamVersion;
import com.github.cwilper.fcrepo.dto.core.MockFedoraObject;
import com.github.cwilper.fcrepo.store.core.FedoraStoreSession;
import com.github.cwilper.fcrepo.store.util.IdSpec;
import com.github.cwilper.fcrepo.store.util.filters.IfPidMatches;
import com.github.cwilper.fcrepo.store.util.filters.NonMutatingFilter;
import com.github.cwilper.fcrepo.store.util.filters.ds.SetFixity;


@RunWith(MockitoJUnitRunner.class)
public class VerifyCopyCommandTest {
    
    private static final String TEST_PID =
            "foo:bar";
    private static final String TEST_DSID =
            "DS";
    private static final byte[] TEST_CONTENT_1 =
            "TEST_CONTENT_1".getBytes();
    
    private static final byte[] TEST_CONTENT_2 =
            "TEST_CONTENT_2".getBytes();

    @Mock
    private FedoraStoreSession mockSource;
    
    @Mock
    private FedoraStoreSession mockDestination;
    
    @Mock
    private SetFixity mockFixityFilter;
    
    @Mock
    private Logger mockLogger;
    
    private IdSpec mockIds = new IdSpec(TEST_PID);
    
    private NonMutatingFilter<FedoraObject> mockFilter =
      new IfPidMatches(new IdSpec("all"));

    private VerifyCopyCommand testCommand;

    @BeforeClass
    public static void bootstrap() {
    }
    
    @AfterClass
    public static void cleanUp() {
        
    }
    
    @Before
    public void setUp() {
        testCommand = new VerifyCopyCommand(mockSource, mockDestination,
                mockIds, mockFilter, mockFixityFilter, mockLogger);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testGoodCopy() throws IOException {
        Date now = new Date();
        MockDatastream mockSourceDs = new MockDatastream(TEST_DSID);
        mockSourceDs.versions(new MockDatastreamVersion(TEST_DSID, now)
        .contentMetadata(TEST_CONTENT_1));
        MockDatastream mockDestDs = new MockDatastream(TEST_DSID);
        mockDestDs.versions(new MockDatastreamVersion(TEST_DSID, now)
        .contentMetadata(TEST_CONTENT_1));
        when(mockFixityFilter.accept(eq(mockSourceDs), any(CommandContext.class)))
        .thenReturn(mockSourceDs);
        when(mockFixityFilter.accept(eq(mockDestDs), any(CommandContext.class)))
        .thenReturn(mockDestDs);
        
        FedoraObject mockSourceObject = new MockFedoraObject();
        mockSourceObject.pid(TEST_PID);
        mockSourceObject.putDatastream(mockSourceDs);
        FedoraObject mockdestinationObject = new MockFedoraObject();
        mockdestinationObject.pid(TEST_PID);
        mockdestinationObject.putDatastream(mockDestDs);
        when(mockSource.getObject(TEST_PID)).thenReturn(mockSourceObject);
        when(mockDestination.getObject(TEST_PID)).thenReturn(mockdestinationObject);
        testCommand.execute();
        verify(mockSource).getObject(TEST_PID);
        verify(mockDestination).getObject(TEST_PID);
        verify(mockLogger, times(0)).warn(any(String.class));
        verify(mockLogger, times(0)).error(any(String.class));
    }
    
    @Test
    public void testGoodCopyPostSetFixity() {
        
    }
    
    @Test
    public void testBadCopy() throws IOException {
        Date now = new Date();
        MockDatastream mockSourceDs = new MockDatastream(TEST_DSID);
        mockSourceDs.versions(new MockDatastreamVersion(TEST_DSID, now)
        .contentMetadata(TEST_CONTENT_1));
        MockDatastream mockDestDs = new MockDatastream(TEST_DSID);
        mockDestDs.versions(new MockDatastreamVersion(TEST_DSID, now)
        .contentMetadata(TEST_CONTENT_2));
        when(mockFixityFilter.accept(eq(mockSourceDs), any(CommandContext.class)))
        .thenReturn(mockSourceDs);
        when(mockFixityFilter.accept(eq(mockDestDs), any(CommandContext.class)))
        .thenReturn(mockDestDs);
        
        FedoraObject mockSourceObject = new MockFedoraObject();
        mockSourceObject.pid(TEST_PID);
        mockSourceObject.putDatastream(mockSourceDs);
        FedoraObject mockdestinationObject = new MockFedoraObject();
        mockdestinationObject.pid(TEST_PID);
        mockdestinationObject.putDatastream(mockDestDs);
        when(mockSource.getObject(TEST_PID)).thenReturn(mockSourceObject);
        when(mockDestination.getObject(TEST_PID)).thenReturn(mockdestinationObject);
        testCommand.execute();
        verify(mockSource).getObject(TEST_PID);
        verify(mockDestination).getObject(TEST_PID);
        verify(mockLogger).error(any(String.class));
    }
    
    private static SortedMap<String, Datastream> sortedSingleton(
            String key, Datastream value) {
        return new TreeMap<String, Datastream>(
                Collections.singletonMap(key, value));
    }
}
