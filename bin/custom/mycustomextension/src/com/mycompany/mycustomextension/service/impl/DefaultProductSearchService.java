package com.mycompany.mycustomextension.service.impl;

import com.mycompany.mycustomextension.dao.ProductSearchDao;
import com.mycompany.mycustomextension.service.ProductSearchService;
import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

import javax.annotation.Resource;

public class DefaultProductSearchService implements ProductSearchService
{
    @Resource
    private ProductSearchDao productSearchDao;

    @Resource
    private CommercePriceService commercePriceService;

    @Override
    public ProductModel findProductByCode(final String code)
    {
        return productSearchDao.findProductByCode(code);
    }

    @Override
    public Double findProductPrice(final ProductModel product)
    {
        final PriceInformation priceInformation =
                commercePriceService.getWebPriceForProduct(product);

        if (priceInformation != null
                && priceInformation.getPriceValue() != null)
        {
            return priceInformation.getPriceValue().getValue();
        }

        return null;
    }

    public void setProductSearchDao(final ProductSearchDao productSearchDao)
    {
        this.productSearchDao = productSearchDao;
    }

    public void setCommercePriceService(final CommercePriceService commercePriceService) {

        this.commercePriceService = commercePriceService;

    }
}