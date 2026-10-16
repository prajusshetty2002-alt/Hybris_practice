
package com.mycompany.mycustomextension.cache;

public class CachedProductDetails
{
    private final String productCode;
    private final String description;

    public CachedProductDetails(
            final String productCode,
            final String description)
    {
        this.productCode = productCode;
        this.description = description;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public String getDescription()
    {
        return description;
    }
}
