package com.mycompany.mycustomocc.controllers;

import com.mycompany.mycustomextension.facade.ProductSearchFacade;
import com.mycompany.mycustomocc.data.ProductSearchData;
import de.hybris.platform.core.model.product.ProductModel;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{baseSiteId}/product-search")
public class ProductSearchController
{
    @Resource(name = "myCustomProductSearchFacade")
    private ProductSearchFacade productSearchFacade;

    @GetMapping
    public ResponseEntity<ProductSearchData> searchProduct(
            @RequestParam("code") final String code)
    {
        final ProductModel product =
                productSearchFacade.findProductByCode(code);

        if (product == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        final ProductSearchData data = new ProductSearchData();

        data.setCode(product.getCode());
        data.setName(product.getName());

        final Double price =
                productSearchFacade.findProductPrice(product);

        data.setPrice(price);

        return ResponseEntity.ok(data);
    }

    public void setProductSearchFacade(
            final ProductSearchFacade productSearchFacade)
    {
        this.productSearchFacade = productSearchFacade;
    }
}