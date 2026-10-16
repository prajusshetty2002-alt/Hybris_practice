package com.mycompany.mycustomextension.setup;

import de.hybris.platform.core.initialization.SystemSetupContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCustomPatch {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCustomPatch.class);

    private final String patchId;
    private final List<String> impexPaths = new ArrayList<>();

    public AbstractCustomPatch(final String patchId) {
        this.patchId = patchId;
    }

    public String getPatchId() {
        return patchId;
    }

    protected void addImpex(final String relativeImpexPath) {
        impexPaths.add(relativeImpexPath);
    }

    public abstract void createDataAndAddPathsToImport();


    public void executePatch(final SystemSetupContext context, final MycustomextensionSystemSetup setup) {
        impexPaths.clear();
        createDataAndAddPathsToImport();

        setup.logInfo(context, ">>> Starting execution of Patch: " + patchId);
        LOG.info("Starting execution of Patch: {}", patchId);

        for (final String path : impexPaths) {
            setup.logInfo(context, "  -> Importing patch impex: " + path);
            LOG.info("Importing patch impex: {}", path);

            try {
                setup.importImpexFile(context, path, true, false);
                setup.logInfo(context, "  [SUCCESS] Imported: " + path);
                LOG.info("Successfully imported: {}", path);
            } catch (final Exception e) {
                final String failureMsg = "  [FAILED] Could not import: " + path + " - " + e.getMessage();
                setup.logError(context, failureMsg, e);
                LOG.error(failureMsg, e);
                throw new RuntimeException("Patch " + patchId + " failed at file: " + path, e);
            }
        }

        setup.logInfo(context, ">>> Completed execution of Patch: " + patchId);
        LOG.info("Completed execution of Patch: {}", patchId);
    }
}