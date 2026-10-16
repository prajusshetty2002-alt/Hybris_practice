package com.mycompany.mycustomextension.setup;

public enum Release {
    PATCH_10_26("10_26"),
    PATCH_10_27("10_27");

    private final String releaseId;

    Release(final String releaseId) {
        this.releaseId = releaseId;
    }

    public String getReleaseId() {
        return this.releaseId;
    }
}