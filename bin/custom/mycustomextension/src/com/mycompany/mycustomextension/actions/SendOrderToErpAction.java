package com.mycompany.mycustomextension.actions;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;

import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

public class SendOrderToErpAction
        extends AbstractSimpleDecisionAction<OrderProcessModel>
{
    private static final Logger LOG =
            Logger.getLogger(SendOrderToErpAction.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Transition executeAction(
            final OrderProcessModel process)
    {
        final OrderModel order = process.getOrder();

        LOG.info("Sending order to ERP: " + order.getCode());

        try
        {
            final String erpUrl =
                    "http://localhost:8081/erp/orders";

            final String processCode = process.getCode();

            final String requestBody =
                    order.getCode() + "|" + processCode;

            final String response =
                    restTemplate.postForObject(
                            erpUrl,
                            requestBody,
                            String.class);
            LOG.info(
                    "ERP response: " + response);

            return Transition.OK;
        }
        catch (Exception e)
        {
            LOG.error(
                    "Failed to send order to ERP: "
                            + order.getCode(),
                    e);

            return Transition.NOK;
        }
    }
}