package com.mycompany.mycustomextension.cache;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.Locale;

import org.apache.log4j.Logger;

public class ProductDetailsService
{
    private static final Logger LOG =
            Logger.getLogger(ProductDetailsService.class);

    private ProductDetailsCache productDetailsCache;

    public CachedProductDetails getDetails(
            final ProductModel product,
            final Locale locale)
    {
        if (product == null)
        {
            throw new IllegalArgumentException("Product cannot be null");
        }

        final Locale effectiveLocale =
                locale != null ? locale : Locale.getDefault();

        // PK distinguishes product records even if their codes are identical.
        final String key = product.getPk().toString() + ":" + effectiveLocale.toLanguageTag();

        // 1. Check cache
        final CachedProductDetails cached = productDetailsCache.get(key);

        if (cached != null)
        {
            LOG.info("PRODUCT DETAILS CACHE HIT: " + key);
            return cached;
        }

        // 2. Cache miss: read details from the existing ProductModel
        LOG.info("PRODUCT DETAILS CACHE MISS: " + key);

        final CachedProductDetails details =
                new CachedProductDetails(
                        product.getCode(),
                        product.getDescription(effectiveLocale));

        // 3. Save in cache
        productDetailsCache.put(key, details);

        return details;
    }

    public void invalidate(
            final ProductModel product,
            final Locale locale)
    {
        if (product == null)
        {
            return;
        }

        final Locale effectiveLocale =
                locale != null ? locale : Locale.getDefault();

        final String key = product.getPk().toString()
                + ":" + effectiveLocale.toLanguageTag();

        productDetailsCache.invalidate(key);

        LOG.info("PRODUCT DETAILS CACHE INVALIDATED: " + key);
    }


}