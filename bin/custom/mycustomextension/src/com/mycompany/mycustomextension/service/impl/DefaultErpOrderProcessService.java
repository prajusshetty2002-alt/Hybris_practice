package com.mycompany.mycustomextension.service.impl;

import com.mycompany.mycustomextension.service.ErpOrderProcessService;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;


public class DefaultErpOrderProcessService
        implements ErpOrderProcessService
{
    private static final Logger LOG =
            Logger.getLogger(DefaultErpOrderProcessService.class);


    private BusinessProcessService businessProcessService;

    private ModelService modelService;


    // =========================================================
    // 1. START ERP PROCESS
    // =========================================================

    @Override
    public void startErpProcess(final OrderModel order)
    {
        final String processCode =
                "erpOrderProcess-" +
                        order.getCode() +
                        "-" +
                        System.currentTimeMillis();

        final OrderProcessModel process =
                businessProcessService.createProcess(
                        processCode,
                        "erpOrderProcess");

        process.setOrder(order);

        modelService.save(process);

        businessProcessService.startProcess(process);

        LOG.info(
                "Started ERP process: " +
                        processCode +
                        " for order: " +
                        order.getCode()
        );
    }


    // =========================================================
    // 2. PROCESS ERP RESPONSE
    // =========================================================

    @Override
    public boolean processErpResponse(
            final String processCode,
            final String responseCode)
    {
        final OrderProcessModel process =
                (OrderProcessModel)
                        businessProcessService.getProcess(processCode);

        if (process == null)
        {
            LOG.error(
                    "ERP process not found: " +
                            processCode
            );

            return false;
        }


        final OrderModel order = process.getOrder();


        // Save ERP response code on the order

        order.setErpResponseCode(responseCode);

        modelService.save(order);


        businessProcessService.triggerEvent(
                processCode + "_ERP_ACK"
        );


        LOG.info(
                "ERP response received. " +
                        "Process: " + processCode +
                        ", Response: " + responseCode
        );

        return true;
    }


    // =========================================================
    // GETTERS / SETTERS
    // =========================================================

    public BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }


    public void setBusinessProcessService(
            final BusinessProcessService businessProcessService)
    {
        this.businessProcessService =
                businessProcessService;
    }


    public ModelService getModelService()
    {
        return modelService;
    }


    public void setModelService(
            final ModelService modelService)
    {
        this.modelService =
                modelService;
    }
}