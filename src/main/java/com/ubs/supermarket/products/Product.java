package com.ubs.supermarket.products;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Interface should be implemented by various types of com.ubs.supermarket.basket.products
 *
 * @author saumadip mazumder
 */
public interface Product
{

    String getName();

    /**
     * Return the individual getPrice of the product,
     *  or Returns the special getPrice if exists for the the product based on number of products purchased
     * @return int
     * @param productCount
     */
    double getPrice(Long productCount);


    /**
     * GEt special price map
     * @return
     */
    ConcurrentHashMap<Long, Double> getSpecialPriceMap();

    /**
     * Add special getPrice for the product, per product
     * Follow the following order of entry
     *
     * 3 for 70 then add(3,23.33)
     * 2 for 15 then add(2,7.5)
     *
     * @return
     * @param specialPrice
     */
    Map<Long, Double> addSpecialPrice(Map<Long, Double> specialPrice);

}
