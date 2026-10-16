package com.mycompany.mycustomextension.hooks;

import com.mycompany.mycustomextension.model.AddToCartEmailProcessModel;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

public class AddToCartEmailHook implements CommerceAddToCartMethodHook
{
    private static final Logger LOG =
            Logger.getLogger(AddToCartEmailHook.class);

    private BusinessProcessService businessProcessService;
    private ModelService modelService;


    @Override
    public void beforeAddToCart(final CommerceCartParameter parameters)
    {
        // Nothing required before adding product.
    }


    @Override
    public void afterAddToCart(final CommerceCartParameter parameters,
                               final CommerceCartModification result)
    {
        if (parameters == null || parameters.getCart() == null)
        {
            return;
        }

        final CartModel cart = parameters.getCart();

        if (!(cart.getUser() instanceof CustomerModel))
        {
            LOG.info("Add-to-cart email skipped because user is anonymous.");
            return;
        }

        final CustomerModel customer =
                (CustomerModel) cart.getUser();

        final ProductModel product =
                parameters.getProduct();

        if (product == null)
        {
            LOG.warn("Add-to-cart email skipped because product is null.");
            return;
        }

        try
        {
            final String processCode =
                    "addToCartEmailProcess-"
                            + cart.getCode()
                            + "-"
                            + product.getCode()
                            + "-"
                            + System.currentTimeMillis();


            final AddToCartEmailProcessModel process =
                    (AddToCartEmailProcessModel)
                            getBusinessProcessService().createProcess(
                                    processCode,
                                    "addToCartEmailProcess");


            /*
             * StoreFrontCustomerProcessModel attributes
             */

            process.setCustomer(customer);
            process.setSite(cart.getSite());
            process.setStore(cart.getStore());
            process.setCurrency(cart.getCurrency());
            process.setLanguage(cart.getSite().getDefaultLanguage());

            /*
             * Our custom attributes
             */

            process.setCart(cart);
            process.setProduct(product);


            getModelService().save(process);

            getBusinessProcessService().startProcess(process);


            LOG.info("==============================================");
            LOG.info("ADD TO CART EMAIL PROCESS STARTED");
            LOG.info("Process : " + processCode);
            LOG.info("Customer: " + customer.getUid());
            LOG.info("Product : " + product.getCode());
            LOG.info("Cart    : " + cart.getCode());
            LOG.info("==============================================");
        }
        catch (final Exception e)
        {
            /*
             * added because process should not break add-to-cart if email processing fails.
             */
            LOG.error("Unable to start Add To Cart Email Process", e);
        }
    }


    public BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }


    public void setBusinessProcessService(
            final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
    }


    public ModelService getModelService()
    {
        return modelService;
    }


    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }
}