package com.github.cwilper.fcrepo.store.legacy;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Memory-based implementation of {@link PathRegistry}.
 */
public class MemoryPathRegistry implements PathRegistry {
    private final Map<String, String> map;

    public MemoryPathRegistry() {
        this(16);
    }
    
    public MemoryPathRegistry(int initialCapacity) {
        map = new HashMap<String, String>(initialCapacity);
    }
    
    @Override
    public long getPathCount() {
        return map.size();
    }

    @Override
    public String getPath(String id) {
        return map.get(id);
    }

    @Override
    public void setPath(String id, String path, PathAlgorithm alg) {
        if (path == null) {
            map.remove(path);
        } else {
            String oldPath = map.get(id);
            if (oldPath != null) {
                GregorianCalendar oc = alg.dateOf(oldPath);
                GregorianCalendar nc = alg.dateOf(path);
                if (oc != null && nc != null &&
                        nc.compareTo(oc) > 0) {
                    map.put(id, path);
                }
            } else {
                map.put(id, path);
            }
        }
    }
}
