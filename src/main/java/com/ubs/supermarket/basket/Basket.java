package com.ubs.supermarket.basket;

import com.ubs.supermarket.products.Product;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Interface should be implemented by various types of baskets
 *
 * @author saumadip mazumder
 */
public interface Basket
{

    /**
     * Returns id to uniquely identify a basket
     * @return
     */
    UUID getBasketID();

    /**
     * Return the current getPrice of the com.ubs.supermarket.basket after addition of the com.ubs.supermarket.basket.products
     * @return double
     */
    Map<Product, Long> addProducts(Collection<? extends Product> products);

    /**
     * Return the current getPrice of the com.ubs.supermarket.basket after removal of the com.ubs.supermarket.basket.products
     * @return double
     */
    Map<Product, Long> removeProducts(Collection<? extends Product> products);

    /**
     * Returns total getPrice of the com.ubs.supermarket.basket
     * @return
     */
    double getTotalPrice();


    /**
     * Returns all com.ubs.supermarket.basket.products in a com.ubs.supermarket.basket
     * @return
     */
    Map<Product, Long> getAllProducts();

}
