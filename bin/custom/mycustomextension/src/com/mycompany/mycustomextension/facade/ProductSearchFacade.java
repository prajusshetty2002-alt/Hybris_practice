package com.mycompany.mycustomextension.facade;

import de.hybris.platform.core.model.product.ProductModel;

public interface ProductSearchFacade
{
    ProductModel findProductByCode(String code);

    Double findProductPrice(ProductModel product);
}