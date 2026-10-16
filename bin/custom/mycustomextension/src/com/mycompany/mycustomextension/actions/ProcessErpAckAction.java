package com.mycompany.mycustomextension.actions;

import com.mycompany.mycustomextension.enums.ErpOrderStatus;

import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;

public class ProcessErpAckAction
        extends AbstractSimpleDecisionAction<OrderProcessModel>
{
    private static final Logger LOG =
            Logger.getLogger(ProcessErpAckAction.class);


    private ModelService modelService;


    @Override
    public Transition executeAction(
            final OrderProcessModel process)
    {
        final OrderModel order = process.getOrder();

        final String responseCode =
                order.getErpResponseCode();


        LOG.info(
                "Processing ERP response for order: "
                        + order.getCode());

        LOG.info(
                "ERP response code: "
                        + responseCode);


        if ("200".equals(responseCode))
        {
            order.setErpOrderStatus(
                    ErpOrderStatus.ERP_CONFIRMED);

            modelService.save(order);

            LOG.info(
                    "ERP order confirmed: "
                            + order.getCode());

            return Transition.OK;
        }


        order.setErpOrderStatus(
                ErpOrderStatus.ERP_REJECTED);

        modelService.save(order);


        LOG.info(
                "ERP order rejected: "
                        + order.getCode());

        return Transition.NOK;
    }


    public ModelService getModelService()
    {
        return modelService;
    }


    public void setModelService(
            final ModelService modelService)
    {
        this.modelService = modelService;
    }
}