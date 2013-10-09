package com.github.cwilper.fcrepo.store.jcr;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.cwilper.fcrepo.dto.core.io.DTOReader;
import com.github.cwilper.fcrepo.dto.core.io.DTOWriter;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLReader;
import com.github.cwilper.fcrepo.dto.foxml.FOXMLWriter;
import com.github.cwilper.fcrepo.store.core.FedoraStore;
import org.junit.Test;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.Session;

/**
 * Unit tests for {@link JCRFedoraStore}.
 */
public class JCRFedoraStoreTest {
    @Test (expected=NullPointerException.class)
    public void initWithNullRepository() throws Exception {
        new JCRFedoraStore(null, mock(Credentials.class),
                mock(DTOReader.class),
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullCredentials() throws Exception {
        new JCRFedoraStore(mock(Repository.class), null,
                mock(DTOReader.class),
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullReaderFactory() throws Exception {
        new JCRFedoraStore(mock(Repository.class),
                mock(Credentials.class),
                null,
                mock(DTOWriter.class));
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullWriterFactory() throws Exception {
        new JCRFedoraStore(mock(Repository.class),
                mock(Credentials.class),
                mock(DTOReader.class),
                null);
    }
    
    @Test
    public void getSession() throws Exception {
        Repository repository = mock(Repository.class);
        Credentials credentials = mock(Credentials.class);
        Session session = mock(Session.class);
        when(repository.login(credentials)).thenReturn(session);
        FedoraStore store = new JCRFedoraStore(repository, credentials,
                new FOXMLReader(), new FOXMLWriter());
        store.getSession().close();
        verify(repository).login(credentials);
    }
}
