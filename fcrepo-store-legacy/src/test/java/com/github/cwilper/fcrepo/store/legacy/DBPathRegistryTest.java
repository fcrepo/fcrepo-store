package com.github.cwilper.fcrepo.store.legacy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.GregorianCalendar;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@DBPathRegistry}.
 */
public class DBPathRegistryTest {
    private static final String TABLE = "testTable";
    private static final String ID1 = "id1";
    private static final String ID2 = "id2";
    private static final String PATH1 = "path1";
    private static final String PATH2 = "path2";
    
    private PathAlgorithm mockAlg;

    private static TemporaryDerbyDB db;
    private static DBPathRegistry registry;

    @BeforeClass
    public static void setUpClass() {
      db = new TemporaryDerbyDB();
      registry = new DBPathRegistry(db, TABLE);
    }
    
    @Before
    public void setUp() {
        registry.clear();
        mockAlg = mock(PathAlgorithm.class);
        GregorianCalendar testCalendar1 = new GregorianCalendar(1975, 12, 26);
        GregorianCalendar testCalendar2 = new GregorianCalendar(1975, 12, 27);
        when(mockAlg.dateOf(PATH1))
        .thenReturn(testCalendar1);
        when(mockAlg.dateOf(PATH2))
        .thenReturn(testCalendar2);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullDB() {
        new DBPathRegistry(null, TABLE);
    }

    @Test (expected=NullPointerException.class)
    public void initWithNullTable() {
        new DBPathRegistry(db, null);
    }

    @Test
    public void getInitialPathCount() {
        Assert.assertEquals(0L, registry.getPathCount());
    }

    @Test
    public void setPathExisting() {
        registry.setPath(ID1, PATH1, mockAlg);
        registry.setPath(ID1, PATH2, mockAlg);
        Assert.assertEquals(1L, registry.getPathCount());
        Assert.assertEquals(PATH2, registry.getPath(ID1));
    }

    @Test
    public void setPathNonExisting() {
        registry.setPath(ID1, PATH1, mockAlg);
        Assert.assertEquals(1L, registry.getPathCount());
        Assert.assertEquals(PATH1, registry.getPath(ID1));
    }

    @Test
    public void setPathTwoNonExisting() {
        registry.setPath(ID1, PATH1, mockAlg);
        registry.setPath(ID2, PATH2, mockAlg);
        Assert.assertEquals(2L, registry.getPathCount());
        Assert.assertEquals(PATH1, registry.getPath(ID1));
        Assert.assertEquals(PATH2, registry.getPath(ID2));
    }

    @Test
    public void setPathNullExisting() {
        registry.setPath(ID1, PATH1, mockAlg);
        Assert.assertEquals(1L, registry.getPathCount());
        registry.setPath(ID1, null, mockAlg);
        Assert.assertEquals(0L, registry.getPathCount());
    }

    @Test
    public void setPathNullNonExisting() {
        registry.setPath(ID1, null, mockAlg);
        Assert.assertEquals(0L, registry.getPathCount());
    }

    @Test
    public void getPathNonExisting() {
        Assert.assertNull(registry.getPath(ID1));
    }

    @Test
    public void getPathExisting() {
        registry.setPath(ID1, PATH1, mockAlg);
        Assert.assertEquals(PATH1, registry.getPath(ID1));
    }

    @AfterClass
    public static void tearDownClass() {
        db.delete();
    }
}
