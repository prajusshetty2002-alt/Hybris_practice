
package com.mycompany.mycustomextension.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProductDetailsCache
{
    private static final long TTL_MILLIS = 5 * 60 * 1000L;

    private final ConcurrentMap<String, CacheEntry> entries = new ConcurrentHashMap<>();

    public CachedProductDetails get(final String key)
    {
        final CacheEntry entry = entries.get(key);

        if (entry == null)
        {
            return null;
        }

        if (System.currentTimeMillis() >= entry.expiresAt)
        {
            entries.remove(key, entry);
            return null;
        }

        return entry.details;
    }

    public void put(
            final String key,
            final CachedProductDetails details)
    {
        entries.put(key, new CacheEntry(details, System.currentTimeMillis() + TTL_MILLIS));
    }

    public void invalidate(final String key)
    {
        entries.remove(key);
    }

    private static class CacheEntry
    {
        private final CachedProductDetails details;
        private final long expiresAt;

        private CacheEntry(
                final CachedProductDetails details,
                final long expiresAt)
        {
            this.details = details;
            this.expiresAt = expiresAt;
        }
    }
}
