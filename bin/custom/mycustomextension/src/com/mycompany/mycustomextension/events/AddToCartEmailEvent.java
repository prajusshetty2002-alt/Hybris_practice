package com.mycompany.mycustomextension.events;

import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.order.CartModel;

public class AddToCartEmailEvent extends AbstractCommerceUserEvent
{
    private CartModel cart;
    private ProductModel product;
    private long quantity;

    public CartModel getCart() {return cart;}

    public void setCart(final CartModel cart)
    {
        this.cart = cart;
    }

    public ProductModel getProduct()
    {
        return product;
    }

    public void setProduct(final ProductModel product)
    {
        this.product = product;
    }


    public void setQuantity(final long quantity)
    {
        this.quantity = quantity;
    }
}