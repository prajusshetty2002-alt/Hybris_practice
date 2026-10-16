package com.mycompany.mycustomextension.solr;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;

public class LearningTagValueResolver extends AbstractValueResolver<ProductModel, Object, Object>
{
    @Override
    protected void addFieldValues(
            final InputDocument document,
            final IndexerBatchContext batchContext,
            final IndexedProperty indexedProperty,
            final ProductModel product,
            final ValueResolverContext<Object, Object> resolverContext)
            throws FieldValueProviderException
    {
        final String learningTag = product.getLearningTag();

        if (learningTag != null && !learningTag.isEmpty())
        {
            document.addField(
                    indexedProperty,
                    learningTag,
                    resolverContext.getFieldQualifier());
        }
    }
}