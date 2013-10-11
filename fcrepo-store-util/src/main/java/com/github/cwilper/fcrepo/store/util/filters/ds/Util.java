package com.github.cwilper.fcrepo.store.util.filters.ds;

import com.github.cwilper.fcrepo.dto.core.ControlGroup;
import com.github.cwilper.fcrepo.dto.core.Datastream;
import com.github.cwilper.fcrepo.dto.core.DatastreamVersion;
import com.github.cwilper.fcrepo.dto.core.FedoraObject;
import com.github.cwilper.fcrepo.dto.core.io.ContentResolver;
import com.github.cwilper.fcrepo.dto.core.io.XMLUtil;
import com.github.cwilper.fcrepo.store.core.FedoraStoreSession;
import com.github.cwilper.fcrepo.store.core.NotFoundException;
import com.github.cwilper.fcrepo.store.core.StoreException;
import com.github.cwilper.fcrepo.store.util.commands.CommandContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    private Util() { }
    
    static boolean isXML(String mimeType) {
        return mimeType != null &&
                (mimeType.endsWith("/xml") || mimeType.endsWith("+xml"));
    }

    static InputStream getInputStream(String info, String pid, Datastream ds,
            DatastreamVersion dsv, ContentResolver contentResolver,
            String localFedoraServer, CommandContext context) throws IOException {
        try {
            if (ds.controlGroup() == ControlGroup.INLINE_XML) {
                if (!dsv.inlineXML().canonical()) {
                    try {
                        XMLUtil.canonicalize(dsv.inlineXML().bytes());
                    } catch (IOException e) {
                        logger.warn("Unable to canonicalize {} using C14N11;"
                                + " using non-standard method ("
                                + e.getCause().getMessage() + ")", info);
                    }
                }
                return new ByteArrayInputStream(dsv.inlineXML().bytes());
            } else if (ds.controlGroup() == ControlGroup.MANAGED) {
                return context.getSource().getContent(pid,
                        ds.id(), dsv.id());
            } else {
                String location = dsv.contentLocation().toString();
                location = location.replace("local.fedora.server",
                        localFedoraServer);
                return contentResolver.resolveContent(null,
                        URI.create(location));
            }
        } catch (StoreException e) {
            throw new IOException(e);
        }
    }

    static long computeSize(String info, String pid, Datastream ds,
            DatastreamVersion dsv, ContentResolver contentResolver,
            String localFedoraServer, CommandContext context) throws IOException {
        try {
            if (ds.controlGroup() == ControlGroup.INLINE_XML) {
                if (!dsv.inlineXML().canonical()) {
                    try {
                        XMLUtil.canonicalize(dsv.inlineXML().bytes());
                    } catch (IOException e) {
                        logger.warn("Unable to canonicalize {} using C14N11;"
                                + " using non-standard method ("
                                + e.getCause().getMessage() + ")", info);
                    }
                }
                return dsv.inlineXML().bytes().length;
            } else if (ds.controlGroup() == ControlGroup.MANAGED) {
                return context.getSource().getContentLength(pid,
                        ds.id(), dsv.id());
            } else {
                String location = dsv.contentLocation().toString();
                location = location.replace("local.fedora.server",
                        localFedoraServer);
                return computeSize(contentResolver.resolveContent(null,
                        URI.create(location)));
            }
        } catch (StoreException e) {
            throw new IOException(e);
        }
    }

    static String[] computeFixity(InputStream inputStream,
            String algorithm) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[4096];
            int count;
            long size = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                size += count;
                digest.update(buffer, 0, count);
            }
            return new String[] { Long.toString(size), hexString(digest.digest()) };
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static final char[] HEX_CHARS = new char[]{
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String hexString(byte[] array) {
        char[] chars = new char[array.length * 2];
        int pos = 0;
        for (int i=0; i< array.length; i++) {
            int v1 = array[i] >>> 4 & 0x0f;
            int v2 = array[i] & 0x0f;
            chars[pos++] = HEX_CHARS[v1];
            chars[pos++] = HEX_CHARS[v2];
        }
        return new String(chars);
    }

    public static byte[] hexStringtoByteArray(String str) {
        int sLen = str.length();
        if ((sLen & 0x01) != 0) {
            throw new NumberFormatException();
        }
        byte ret[] = new byte[sLen / 2];
        for (int i = 0; i < sLen; i+=2) {
            ret[i/2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) +
                     Character.digit(str.charAt(i+1), 16));
        }
        return ret;
    }

    static long computeSize(InputStream inputStream)
            throws IOException {
        try {
            return IOUtils.skip(inputStream, Long.MAX_VALUE);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    // create or update an existing object so it refers to the given
    // managed datastream -- a prerequisite to putting the content into
    // the store.
    static void putObjectIfNoSuchManagedDatastream(FedoraObject object,
            FedoraStoreSession store, String datastreamId) {
        try {
            FedoraObject existing = store.getObject(object.pid());
            Datastream ds = existing.datastreams().get(datastreamId);
            if (ds == null || ds.controlGroup() != ControlGroup.MANAGED) {
                store.updateObject(object);
            }
        } catch (NotFoundException e) {
            store.addObject(object);
        }
    }
}
