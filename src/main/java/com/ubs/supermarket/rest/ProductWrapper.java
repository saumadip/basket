package com.ubs.supermarket.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ubs.supermarket.products.ProductImpl;

import java.util.List;

public class ProductWrapper
{
    private List<ProductImpl> productList;

    @JsonCreator
    public ProductWrapper(@JsonProperty("productlist")List<ProductImpl> productList) {
        this.productList = productList;
    }

    public List<ProductImpl> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductImpl> productList) {
        this.productList = productList;
    }
}
