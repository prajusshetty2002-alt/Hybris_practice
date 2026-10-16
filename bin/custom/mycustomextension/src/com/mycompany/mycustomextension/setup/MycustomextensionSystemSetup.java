/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.mycompany.mycustomextension.setup;

import static com.mycompany.mycustomextension.constants.MycustomextensionConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mycompany.mycustomextension.constants.MycustomextensionConstants;
import com.mycompany.mycustomextension.service.MycustomextensionService;

@SystemSetup(extension = MycustomextensionConstants.EXTENSIONNAME)
public class MycustomextensionSystemSetup extends AbstractSystemSetup
{
    private final MycustomextensionService mycustomextensionService;
    private List<AbstractCustomPatch> patches = Collections.emptyList();

    public MycustomextensionSystemSetup(final MycustomextensionService mycustomextensionService)
    {
        this.mycustomextensionService = mycustomextensionService;
    }

    public void setPatches(final List<AbstractCustomPatch> patches)
    {
        this.patches = patches;
    }

    @SystemSetup(process = SystemSetup.Process.ALL, type = SystemSetup.Type.ESSENTIAL)
    public void createEssentialData()
    {
        mycustomextensionService.createLogo(PLATFORM_LOGO_CODE);
    }

    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
    public void createProjectData(final SystemSetupContext context)
    {
        logInfo(context, "==================================================");
        logInfo(context, "Hotswap setup started for " + context.getExtensionName());
        logInfo(context, "==================================================");

        if (patches == null || patches.isEmpty())
        {
            logInfo(context, "No patches registered for " + context.getExtensionName());
            return;
        }

        for (final AbstractCustomPatch patch : patches)
        {
            final String fullKey = context.getExtensionName() + "_patch_" + patch.getPatchId();
            final String shortKey = "patch_" + patch.getPatchId();

            String paramValue = context.getParameter(fullKey);
            if (paramValue == null)
            {
                paramValue = context.getParameter(shortKey);
            }

            if ("yes".equalsIgnoreCase(paramValue) || "true".equalsIgnoreCase(paramValue))
            {
                logInfo(context, "Patch " + patch.getPatchId() + " selected: YES -> Running...");

                patch.executePatch(context, this);
            }
            else
            {
                logInfo(context, "Patch " + patch.getPatchId() + " skipped (value was not 'yes')");
            }
        }

        logInfo(context, "==================================================");
        logInfo(context, "Project Data setup finished for " + context.getExtensionName());
        logInfo(context, "==================================================");
    }

    @SystemSetupParameterMethod
    @Override
    public List<SystemSetupParameter> getInitializationOptions()
    {
        final List<SystemSetupParameter> params = new ArrayList<>();
        if (patches != null && !patches.isEmpty())
        {
            final int totalPatches = patches.size();

            for (int i = 0; i < totalPatches; i++)
            {
                final AbstractCustomPatch patch = patches.get(i);

                // Only true for the last patch in the list
                final boolean isLatest = (i == totalPatches - 1);

                final SystemSetupParameter param = new SystemSetupParameter("patch_" + patch.getPatchId());
                param.setLabel("Patch " + patch.getPatchId());
                param.addValue("yes", !isLatest);
                param.addValue("no", isLatest);

                params.add(param);
            }
        }
        return params;
    }

    private InputStream getImageStream()
    {
        return MycustomextensionSystemSetup.class.getResourceAsStream("/mycustomextension/sap-hybris-platform.png");
    }
}