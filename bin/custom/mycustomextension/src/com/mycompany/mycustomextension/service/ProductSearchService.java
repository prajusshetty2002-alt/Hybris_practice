package com.mycompany.mycustomextension.service;

import de.hybris.platform.core.model.product.ProductModel;

public interface ProductSearchService
{
    ProductModel findProductByCode(String code);

    Double findProductPrice(ProductModel product);
}