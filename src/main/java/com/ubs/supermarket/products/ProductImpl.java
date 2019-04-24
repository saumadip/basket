package com.ubs.supermarket.products;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.CollectionUtils;

import javax.naming.directory.InvalidAttributesException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Thread safe Product implementation of the following methods
 *
 * 1. public String getName()
 *
 * 2. public double getPrice(Long productCount)
 *
 * 3. public int addSpecialPrice(Map<Integer, Double> specialPriceList)
 *
 * @author saumadip mazumder
 */
public class ProductImpl implements Product
{
    @JsonProperty("name")
    private final String name;

    @JsonProperty("price")
    private final double price;

    @JsonProperty("specialPriceMap")
    private final ConcurrentHashMap<Long, Double> specialPriceMap;


    @JsonCreator
    public ProductImpl(@JsonProperty("name") String name, @JsonProperty("price") double price) throws InvalidAttributesException
    {

        this.name = name;

        this.price = price;

        if(price <= 0)
            throw new InvalidAttributesException("Price cannot be negetive or 0");

        this.specialPriceMap = new ConcurrentHashMap<>();

    }

    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * Add special getPrice for the product, per product
     * Follow the following order of entry
     *
     * 3 for 70 then add(3,23.33)
     * 2 for 15 then add(2,7.5)
     *
     * @return
     */
    @Override
    public double getPrice(Long productCount)
    {
        if(productCount == 0 || productCount < 0)
            return 0;

        if(productCount == 1)
            return price;

        if(!CollectionUtils.isEmpty(specialPriceMap))
        {
            ArrayList<Long> keys = Collections.list(specialPriceMap.keys());

            for(int i=0;i < keys.size();i++)
            {
                Long noOfProductsInDiscount = keys.get(i);

                if(productCount.equals(noOfProductsInDiscount))
                {
                    return specialPriceMap.get(noOfProductsInDiscount) * productCount;
                }
                else if(noOfProductsInDiscount > productCount )
                {

                    if( i > 0) {
                        noOfProductsInDiscount = keys.get(i - 1);
                        long remainingProducts = productCount - noOfProductsInDiscount;
                        return getPrice(remainingProducts) + noOfProductsInDiscount * specialPriceMap.get(noOfProductsInDiscount);
                    }
                    else
                    {
                        return price * productCount;
                    }

                }
                else if( i == keys.size() -1)
                {
                    noOfProductsInDiscount = keys.get(i);

                    long remainingProducts = productCount - noOfProductsInDiscount;

                    return getPrice(remainingProducts) + noOfProductsInDiscount * specialPriceMap.get(noOfProductsInDiscount);
                }

            }
        }

        return price * productCount;

    }


    /**
     * Update special offer prices
     *
     *  Map<Long, Double> specialPrice2 = new ConcurrentHashMap<>();
     *         specialPrice.put(3L,2.0);
     *         specialPrice.put(4L,4.0);
     *         specialPrice.put(7L,5.0);
     * @param specialPriceUpdate
     * @return
     */
    @Override
    public Map<Long, Double> addSpecialPrice(Map<Long, Double> specialPriceUpdate)
    {
        specialPriceUpdate.forEach((key,val)->
        {
            if(key > 0 && val > 0)
                specialPriceMap.merge(key, val, (v1, v2) -> v1 = v2);
        });
        return Collections.unmodifiableMap(specialPriceMap);
    }

    public ConcurrentHashMap<Long, Double> getSpecialPriceMap() {
        return specialPriceMap;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProductImpl)) return false;

        ProductImpl product = (ProductImpl) o;

        return new EqualsBuilder()
                .append(price, product.price)
                .append(getName(), product.getName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getName())
                .append(price)
                .toHashCode();
    }
}
