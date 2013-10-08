package com.github.cwilper.fcrepo.store.legacy;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Unit tests for {@link FilesystemPathIterator}.
 */
public class FilesystemPathIteratorTest {
    private static File tempDir;
    private static File emptyDir;
    private static File nonEmptyDir;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        tempDir = File.createTempFile("fcrepo-store-legacy-test", null);
        tempDir.delete();
        tempDir.mkdir();
        emptyDir = new File(tempDir, "emptyDir");
        emptyDir.mkdir();
        nonEmptyDir = new File(tempDir, "nonEmptyDir");
        nonEmptyDir.mkdir();
        touch(new File(nonEmptyDir, "file1"));
        touch(new File(nonEmptyDir, "file2"));
        touch(new File(new File(nonEmptyDir, "file3"), "file1"));
        touch(new File(nonEmptyDir, "file4"));
        touch(new File(tempDir, "pfile"));
    }
    
    private static void touch(File file) throws Exception {
        OutputStream out;
        file.getParentFile().mkdirs();
        out = new FileOutputStream(file);
        IOUtils.write(new byte[] { 0 }, out);
        out.close();
    }

    @Test
    public void iterateAll() {
        Set<String> paths = toSet(new FilesystemPathIterator(tempDir));
        Assert.assertEquals(5, paths.size());
        Assert.assertTrue(paths.contains("nonEmptyDir/file1"));
        Assert.assertTrue(paths.contains("nonEmptyDir/file2"));
        Assert.assertTrue(paths.contains("nonEmptyDir/file3/file1"));
        Assert.assertTrue(paths.contains("nonEmptyDir/file4"));
        Assert.assertTrue(paths.contains("pfile"));
    }

    @Test
    public void iterateNonEmpty() {
        Set<String> paths = toSet(new FilesystemPathIterator(nonEmptyDir));
        Assert.assertEquals(4, paths.size());
        Assert.assertTrue(paths.contains("file1"));
        Assert.assertTrue(paths.contains("file2"));
    }

    @Test
    public void iterateEmpty() {
        Set<String> paths = toSet(new FilesystemPathIterator(emptyDir));
        Assert.assertEquals(0, paths.size());
    }
    
    private Set<String> toSet(Iterator<String> iter) {
        Set<String> set = new HashSet<String>();
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        return set;
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        rmdirs(tempDir);
    }

    private static void rmdirs(File dir) {
        for (File child : dir.listFiles()) {
            if (child.isDirectory()) {
                rmdirs(child);
            } else {
                child.delete();
            }
        }
        dir.delete();
    }    
}
