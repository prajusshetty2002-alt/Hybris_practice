package com.mycompany.mycustomextension.setup.patches;

import com.mycompany.mycustomextension.setup.AbstractCustomPatch;
import com.mycompany.mycustomextension.setup.Release;

public class Patch_10_27 extends AbstractCustomPatch {

    public Patch_10_27() {
        super(Release.PATCH_10_27.getReleaseId());
    }

    @Override
    public void createDataAndAddPathsToImport() {

        addImpex("/mycustomextension/import/patches/01_customer_update.impex");
        addImpex("/mycustomextension/import/patches/02_catalog_update.impex");
    }
}