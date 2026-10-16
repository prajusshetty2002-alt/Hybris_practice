package com.mycompany.mycustomextension.process.email;

import com.mycompany.mycustomextension.model.AddToCartEmailProcessModel;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class AddToCartEmailContext
        extends AbstractEmailContext<AddToCartEmailProcessModel>
{
    @Override
    public void init(final AddToCartEmailProcessModel process,
                     final EmailPageModel emailPageModel)
    {
        super.init(process, emailPageModel);

        final CustomerModel customer = process.getCustomer();
        final CartModel cart = process.getCart();
        final ProductModel product = process.getProduct();

        if (customer != null)
        {
            put("customerName", customer.getName());
            put("customerEmail", customer.getUid());
        }

        if (cart != null)
        {
            put("cartCode", cart.getCode());
        }

        if (product != null)
        {
            put("productName", product.getName());
            put("productCode", product.getCode());
        }

        put("quantity", "1");
    }

    @Override
    protected BaseSiteModel getSite(
            final AddToCartEmailProcessModel process)
    {
        return process.getSite();
    }

    @Override
    protected CustomerModel getCustomer(
            final AddToCartEmailProcessModel process)
    {
        return process.getCustomer();
    }

    @Override
    protected LanguageModel getEmailLanguage(
            final AddToCartEmailProcessModel process)
    {
        return process.getLanguage();
    }
}