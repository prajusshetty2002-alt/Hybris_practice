package com.mycompany.mycustomextension.search.provider;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

public class ProductRatingValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
    private FieldNameProvider fieldNameProvider;
    private CustomerReviewService customerReviewService;

    protected FieldNameProvider getFieldNameProvider()
    {
        return fieldNameProvider;
    }

    @Required
    public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
    {
        this.fieldNameProvider = fieldNameProvider;
    }

    protected CustomerReviewService getCustomerReviewService()
    {
        return customerReviewService;
    }

    @Required
    public void setCustomerReviewService(
            final CustomerReviewService customerReviewService)
    {
        this.customerReviewService = customerReviewService;
    }

    @Override
    public Collection<FieldValue> getFieldValues(
            final IndexConfig indexConfig,
            final IndexedProperty indexedProperty,
            final Object model)
            throws FieldValueProviderException
    {

        if (!(model instanceof ProductModel))
        {
            throw new FieldValueProviderException(
                    "Cannot evaluate rating of non-product item");
        }

        final ProductModel product = (ProductModel) model;

        final Collection<FieldValue> fieldValues =
                new ArrayList<FieldValue>();

        if (indexedProperty.isLocalized())
        {
            for (final LanguageModel language : indexConfig.getLanguages())
            {
                fieldValues.addAll(
                        createFieldValue(product, language, indexedProperty));
            }
        }
        else
        {
            fieldValues.addAll(createFieldValue(product, null, indexedProperty));
        }

        return fieldValues;
    }

    /**
     * Gets the product's average rating and,
     * if available, prepares the Solr field value.
     */
    protected List<FieldValue> createFieldValue(
            final ProductModel product,
            final LanguageModel language,
            final IndexedProperty indexedProperty)
    {
        final List<FieldValue> fieldValues =
                new ArrayList<FieldValue>();

        // Calculate average rating
        final Double productRating =
                getProductRating(product, language);

        // Only create a Solr value when a rating exists
        if (productRating != null)
        {
            addFieldValues(fieldValues, indexedProperty, language, productRating);
        }

        return fieldValues;
    }

    /**
     * Creates FieldValue objects using the actual Solr field name.
     */
    protected void addFieldValues(
            final List<FieldValue> fieldValues,
            final IndexedProperty indexedProperty,
            final LanguageModel language,
            final Object value)
    {
        final Collection<String> fieldNames =
                getFieldNameProvider().getFieldNames(
                        indexedProperty,
                        language == null
                                ? null
                                : language.getIsocode());

        for (final String fieldName : fieldNames)
        {
            fieldValues.add(new FieldValue(fieldName, value));
        }
    }

    /**
     * Calculates the average rating from approved customer reviews.
     */
    protected Double getProductRating(
            final ProductModel product,
            final LanguageModel language)
    {
        double sum = 0d;
        int count = 0;

        // Get all reviews belonging to this product
        for (final CustomerReviewModel review :
                getCustomerReviewService().getReviewsForProduct(product))
        {

            if ((language == null
                    || language.equals(review.getLanguage()))
                    && review.getRating() != null
                    && review.getApprovalStatus()
                    == CustomerReviewApprovalType.APPROVED)
            {
                count++;
                sum += review.getRating().doubleValue();
            }
        }

        // Calculate average rating
        if (count > 0)
        {
            return Double.valueOf(sum / count);
        }
        return null;
    }
}