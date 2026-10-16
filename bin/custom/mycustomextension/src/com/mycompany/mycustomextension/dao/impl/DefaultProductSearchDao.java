package com.mycompany.mycustomextension.dao.impl;

import com.mycompany.mycustomextension.dao.ProductSearchDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;
import java.util.List;

public class DefaultProductSearchDao implements ProductSearchDao
{
    private static final String FIND_PRODUCT_BY_CODE =
            "SELECT {p.pk} " +
            "FROM {Product AS p} " +
            "WHERE {p.code} = ?code";

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Override
    public ProductModel findProductByCode(final String code)
    {
        final FlexibleSearchQuery query =
                new FlexibleSearchQuery(FIND_PRODUCT_BY_CODE);

        query.addQueryParameter("code", code);

        final List<ProductModel> products =
                flexibleSearchService.<ProductModel>search(query).getResult();

        return products.isEmpty() ? null : products.get(0);
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
    {
        this.flexibleSearchService = flexibleSearchService;
    }
}