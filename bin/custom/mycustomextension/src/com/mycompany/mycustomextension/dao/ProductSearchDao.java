package com.mycompany.mycustomextension.dao;

import de.hybris.platform.core.model.product.ProductModel;

public interface ProductSearchDao
{
    ProductModel findProductByCode(String code);
}