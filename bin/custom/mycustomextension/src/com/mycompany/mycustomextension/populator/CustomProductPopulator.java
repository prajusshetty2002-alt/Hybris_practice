package com.mycompany.mycustomextension.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.commercefacades.product.data.ProductData;

public class CustomProductPopulator implements Populator<ProductModel, ProductData> {
    @Override
    public void populate(final ProductModel source, final ProductData target) {
        if (source.getWarrantyPeriod() != null) {
            target.setWarrantyPeriod(source.getWarrantyPeriod());
        }
    }
}