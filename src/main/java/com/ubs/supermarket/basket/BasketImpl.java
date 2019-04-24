package com.ubs.supermarket.basket;

import com.ubs.supermarket.products.Product;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * Thread safe Basket implementation of the following methods
 *
 * 1. public Map<Product, Long> addProducts(Collection<? extends Product> products)
 *
 * 2. public Map<Product, Long> removeProducts(Collection<? extends Product> products)
 *
 * 3. public double getTotalPrice()
 *
 * 4. public Map<Product, Long> getAllProducts()
 *
 * @author saumadip mazumder
 */
public class BasketImpl implements Basket
{

    /**
     * In memory datastructure for storing basket information
     */
    //@JsonSerialize(keyUsing = ProductSerializer.class)
    private final ConcurrentHashMap<Product,Long> basketCollection;

    /**
     * uniquely identifies a basket
     */
    private final UUID basketID;


    public BasketImpl(ConcurrentHashMap<Product, Long> basketCollection)
    {
        this.basketCollection = basketCollection;
        this.basketID = UUID.randomUUID();
    }


    /**
     * return the basketID
     * @return
     */
    @Override
    public UUID getBasketID() {
        return basketID;
    }

    /**
     * Adds a collection of products to the basket
     * @param products
     * @return
     */
    @Override
    public Map<Product, Long> addProducts(Collection<? extends Product> products)
    {
        if(!CollectionUtils.isEmpty(products))
        {
            products.forEach(this::updateLatestPromotionToBasket);

        }
        return Collections.unmodifiableMap(basketCollection);
    }

    /**
     * Removes collection of products from the basket,
     * Ignores product removal if product is not in basket.
     * @param products
     * @return
     */
    @Override
    public Map<Product, Long> removeProducts(Collection<? extends Product> products)
    {
        if(!CollectionUtils.isEmpty(products)) {

            Map<? extends Product, Long> groupedProductcollection = products.stream().collect(groupingBy(Function.identity(), Collectors.counting()));

            synchronized (basketCollection) {
                groupedProductcollection.forEach((key, val) ->
                {
                    Long oldVal = basketCollection.get(key);
                    if(oldVal != null)
                    {
                        long newVal = (oldVal - val) < 0 ? 0 : oldVal - val;

                        if(newVal > 0)
                            basketCollection.replace(key, oldVal, newVal);
                        else
                            basketCollection.remove(key,oldVal);
                    }
                });
            }
        }
        return Collections.unmodifiableMap(basketCollection);
    }

    /**
     * Total price of the basket
     * @return
     */
    @Override
    public double getTotalPrice()
    {
        return basketCollection.entrySet()
                .stream()
                .map(entry -> entry.getKey().getPrice(entry.getValue()))
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    /**
     * Returns all the products in the basket
     * @return
     */
    @Override
    public Map<Product, Long> getAllProducts()
    {
        return Collections.unmodifiableMap(basketCollection);
    }


    /**
     * Updates the latest promotion that came with the new Product in the basket.
     * Applying promotion to all products of the same name or category
     * @param prods
     */
    private void updateLatestPromotionToBasket(Product prods)
    {
        synchronized (basketCollection)
        {
            if (basketCollection.containsKey(prods))
            {
                Long oldVal = basketCollection.get(prods);
                basketCollection.remove(prods);
                basketCollection.putIfAbsent(prods,oldVal + 1);
            }
            else
            {
                basketCollection.putIfAbsent(prods, 1L);
            }
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof BasketImpl)) return false;

        BasketImpl basket = (BasketImpl) o;

        return new EqualsBuilder()
                .append(getBasketID(), basket.getBasketID())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getBasketID())
                .toHashCode();
    }
}
