package com.mycompany.mycustomextension.setup.patches;

import com.mycompany.mycustomextension.setup.AbstractCustomPatch;
import com.mycompany.mycustomextension.setup.Release;

public class Patch_10_26 extends AbstractCustomPatch {

    public Patch_10_26() {
        super(Release.PATCH_10_26.getReleaseId());
    }

    @Override
    public void createDataAndAddPathsToImport() {
        addImpex("/mycustomextension/import/patches/sample_data_update.impex");
        addImpex("/mycustomextension/import/patches/02_sample_data_update.impex");
    }
}