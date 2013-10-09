package com.github.cwilper.fcrepo.store.util.commands;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.store.core.FedoraStoreSession;


public class VerifyCopyCommandTest {
    
    private FedoraStoreSession mockSource;
    
    private FedoraStoreSession mockDestination;

    @BeforeClass
    public static void bootstrap() {
    }
    
    @AfterClass
    public static void cleanUp() {
        
    }
    
    @Before
    public void setUp() {
        mockSource = mock(FedoraStoreSession.class);
        mockDestination = mock(FedoraStoreSession.class);
    }
    
    @After
    public void tearDown() {
        
    }
    
    @Test
    public void testGoodCopy() {
        String testPid = "foo:bar";
        FedoraObject mockSourceObject = mock(FedoraObject.class);
        FedoraObject mockdestinationObject = mock(FedoraObject.class);
        when(mockSource.getObject(testPid)).thenReturn(mockSourceObject);
        when(mockDestination.getObject(testPid)).thenReturn(mockdestinationObject);
        //verify(mockSource).getObject(testPid);
        //verify(mockDestination).getObject(testPid);
    }
    
    @Test
    public void testGoodCopyPostSetFixity() {
        
    }
    
    @Test
    public void testBadCopy() {
        
    }
}
