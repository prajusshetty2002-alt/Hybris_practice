package com.mycompany.mycustomextension.process.actions;

import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrepareAddToCartEmailAction
        extends AbstractSimpleDecisionAction<BusinessProcessModel>
{
    private static final Logger LOG =
            LoggerFactory.getLogger(PrepareAddToCartEmailAction.class);

    @Override
    public Transition executeAction(final BusinessProcessModel process)
    {
        LOG.info("==========================================");
        LOG.info("ADD TO CART EMAIL PROCESS STARTED");
        LOG.info("Process Code: {}", process.getCode());
        LOG.info("Preparing add-to-cart email...");
        LOG.info("==========================================");

        return Transition.OK;
    }
}