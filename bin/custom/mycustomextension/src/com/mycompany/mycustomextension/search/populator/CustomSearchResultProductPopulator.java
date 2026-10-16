package com.mycompany.mycustomextension.search.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

public class CustomSearchResultProductPopulator
        implements Populator<SearchResultValueData, ProductData>
{
    @Override
    public void populate(final SearchResultValueData source, final ProductData target)
    {
        final Object rating = source.getValues().get("rating_double");

        if (rating != null)
        {
            target.setAverageRating(
                    Double.valueOf(rating.toString()));
        }
    }
}