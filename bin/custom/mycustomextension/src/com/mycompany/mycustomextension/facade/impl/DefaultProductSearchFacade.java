package com.mycompany.mycustomextension.facade.impl;

import com.mycompany.mycustomextension.facade.ProductSearchFacade;
import com.mycompany.mycustomextension.service.ProductSearchService;
import de.hybris.platform.core.model.product.ProductModel;

import javax.annotation.Resource;

public class DefaultProductSearchFacade implements ProductSearchFacade
{
    @Resource
    private ProductSearchService productSearchService;

    @Override
    public ProductModel findProductByCode(final String code)
    {
        return productSearchService.findProductByCode(code);
    }

    @Override
    public Double findProductPrice(final ProductModel product)
    {
        return productSearchService.findProductPrice(product);
    }

    public void setProductSearchService(final ProductSearchService productSearchService)
    {
        this.productSearchService = productSearchService;
    }
}