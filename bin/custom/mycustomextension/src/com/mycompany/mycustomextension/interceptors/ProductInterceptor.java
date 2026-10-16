package com.mycompany.mycustomextension.interceptors;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class ProductInterceptor implements PrepareInterceptor<ProductModel>, ValidateInterceptor<ProductModel>, RemoveInterceptor<ProductModel>
{
    /*
     * Rule:
     * If description is empty, automatically set a default value.
     */
    @Override
    public void onPrepare(final ProductModel product,
                          final InterceptorContext context)
            throws InterceptorException
    {
        if (product.getDescription() == null
                || product.getDescription().trim().isEmpty())
        {
            product.setDescription("Product description not available");
        }
    }


    /*
     *
     * Rule 1:
     * Product name cannot be empty.
     *
     * Rule 2:
     * Product code cannot be empty.
     */
    @Override
    public void onValidate(final ProductModel product,
                           final InterceptorContext context)
            throws InterceptorException
    {
        if (product.getName() == null
                || product.getName().trim().isEmpty())
        {
            throw new InterceptorException(
                    "Product name cannot be empty."
            );
        }

        if (product.getCode() == null
                || product.getCode().trim().isEmpty())
        {
            throw new InterceptorException(
                    "Product code cannot be empty."
            );
        }
    }


    /*
     * Runs before Product is removed.
     *
     * Rule:
     * Product with code "PROTECTED-001" cannot be deleted.
     */
    @Override
    public void onRemove(final ProductModel product,
                         final InterceptorContext context)
            throws InterceptorException
    {
        if ("PROTECTED-001".equals(product.getCode()))
        {
            throw new InterceptorException(
                    "Protected product cannot be deleted."
            );
        }
    }
}