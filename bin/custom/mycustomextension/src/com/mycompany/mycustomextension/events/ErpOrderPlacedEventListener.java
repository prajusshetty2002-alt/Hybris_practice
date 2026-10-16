package com.mycompany.mycustomextension.events;

import de.hybris.platform.orderprocessing.events.OrderPlacedEvent;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import com.mycompany.mycustomextension.service.ErpOrderProcessService;


public class ErpOrderPlacedEventListener
        extends AbstractEventListener<OrderPlacedEvent>
{
    private ErpOrderProcessService erpOrderProcessService;


    @Override
    protected void onEvent(final OrderPlacedEvent event)
    {
        final OrderModel order =
                event.getProcess().getOrder();

        if (order == null)
        {
            return;
        }

        erpOrderProcessService.startErpProcess(order);
    }


    public ErpOrderProcessService getErpOrderProcessService()
    {
        return erpOrderProcessService;
    }


    public void setErpOrderProcessService(
            final ErpOrderProcessService erpOrderProcessService)
    {
        this.erpOrderProcessService =
                erpOrderProcessService;
    }
}