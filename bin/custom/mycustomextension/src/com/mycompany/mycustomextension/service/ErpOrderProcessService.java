package com.mycompany.mycustomextension.service;

import de.hybris.platform.core.model.order.OrderModel;

public interface ErpOrderProcessService
{
    void startErpProcess(OrderModel order);

    boolean processErpResponse(
            String processCode,
            String responseCode);
}