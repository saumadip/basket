package com.ubs.supermarket.basket;

import com.ubs.supermarket.products.Product;
import com.ubs.supermarket.products.ProductImpl;
import org.junit.Before;
import org.junit.Test;

import javax.naming.directory.InvalidAttributesException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class BasketImplTest
{

    private Basket basket;

    private List<Product> products;

    private ConcurrentHashMap<Product,Long> basketCollection;

    @Before
    public void setUp()throws InvalidAttributesException
    {
        basketCollection = new ConcurrentHashMap<>();
        basket = new BasketImpl(basketCollection);

        products = new ArrayList<Product>();

        products.addAll(Arrays.asList( new ProductImpl("P1",10),
                new ProductImpl("P2",15),
                new ProductImpl("P3",20),
                new ProductImpl("P4",30),
                new ProductImpl("P5",30)));
    }

    @Test
    public void addProducts() throws InvalidAttributesException
    {
        ProductImpl newProduct = new ProductImpl("P1", 10);
        products.add(newProduct);


        Map<Product, Long> productLongMap = basket.addProducts(products);

        productLongMap.forEach((key,value)->{

            assertTrue(products.contains(key));

            if(key.getName().equals(newProduct.getName()))
                assertEquals(2L,value,0);
            else
                assertEquals(1L,value,0);
        });


    }

    @Test
    public void removeProducts() throws InvalidAttributesException
    {
        ProductImpl newProduct = new ProductImpl("P1", 10);
        products.add(newProduct);

        List<Product> productsToRemove = new ArrayList<Product>();

        ProductImpl p5 = new ProductImpl("P5", 30);
        productsToRemove.addAll(Arrays.asList( new ProductImpl("P1",10),
                p5));

        basket.addProducts(products);

        Map<Product, Long> productLongMap = basket.removeProducts(productsToRemove);

        assertTrue(!productLongMap.containsKey(p5));

        assertTrue(productLongMap.containsKey(newProduct));

        assertEquals(1L,productLongMap.get(newProduct),0);

        productLongMap.forEach((key,value)->{

            assertTrue(products.contains(key));
            assertEquals(1L,value,0);
        });
    }

    @Test
    public void removeNonexistingProducts() throws InvalidAttributesException
    {

        List<Product> productsToRemove = new ArrayList<Product>();

        ProductImpl p6= new ProductImpl("P6", 30);
        ProductImpl p1 = new ProductImpl("P1", 10);
        productsToRemove.addAll(Arrays.asList(p1,
                p6));

        basket.addProducts(products);

        Map<Product, Long> productLongMap = basket.removeProducts(productsToRemove);

        assertTrue(!productLongMap.containsKey(p6));
        assertTrue(!productLongMap.containsKey(p1));


        productLongMap.forEach((key,value)->{

            assertTrue(products.contains(key));
            assertEquals(1L,value,0);
        });
    }

    @Test
    public void getTotalPriceWithoutPromotion()
    {
        basket.addProducts(products);

        double expectedTotalPrice=0.0;

        for(Product tempProd : products)
        {
            expectedTotalPrice = expectedTotalPrice + tempProd.getPrice(1L);
        }

        assertEquals(expectedTotalPrice,basket.getTotalPrice(),0);
    }


    @Test
    public void getTotalPriceWithPromotion() throws InvalidAttributesException {


        Map<Long, Double> specialPrice = new ConcurrentHashMap<>();
        specialPrice.put(3L,5.0);
        specialPrice.put(6L,3.0);

        ProductImpl newProduct = new ProductImpl("P1", 10);
        newProduct.addSpecialPrice(specialPrice);
        products.add(newProduct);
        products.add(newProduct);
        products.add(newProduct);

        basket.addProducts(products);

        /**
         *  as there are 4 P1 products in the basket and rule says
         *  by 3 then price is 5 for each
         */
        double expectedPromotionalPrice = 15 + 10;
        double expectedTotalPrice=0.0;

        for(Product tempProd : products)
        {
            if(!tempProd.equals(newProduct))
                expectedTotalPrice = expectedTotalPrice + tempProd.getPrice(1L);

        }

        expectedTotalPrice = expectedTotalPrice + expectedPromotionalPrice;

        assertEquals(expectedTotalPrice,basket.getTotalPrice(),0);
    }


    @Test
    public void getTotalPriceWithUpdatingPromotion() throws InvalidAttributesException {


        Map<Long, Double> specialPrice = new ConcurrentHashMap<>();
        specialPrice.put(3L,5.0);
        specialPrice.put(6L,3.0);

        ProductImpl newProduct = new ProductImpl("P1", 10);
        newProduct.addSpecialPrice(specialPrice);
        products.add(newProduct);
        products.add(newProduct);
        products.add(newProduct);

        basket.addProducts(products);

        Map<Long, Double> specialPriceUpdate = new ConcurrentHashMap<>();
        specialPrice.put(3L,4.0);
        specialPrice.put(6L,2.0);

        ProductImpl newProductWithUpdatedPromotion = new ProductImpl("P1", 10);
        newProductWithUpdatedPromotion.addSpecialPrice(specialPrice);


        basket.addProducts(Arrays.asList(newProductWithUpdatedPromotion));

        /**
         *  as there are 5 P1 products in the basket and rule says
         *  by 3 then price is 4 for each and 2 will be normal price of 10
         */
        double expectedPromotionalPrice = 12 + 20;
        double expectedTotalPrice=0.0;

        for(Product tempProd : products)
        {
            if(!tempProd.equals(newProduct))
                expectedTotalPrice = expectedTotalPrice + tempProd.getPrice(1L);

        }

        expectedTotalPrice = expectedTotalPrice + expectedPromotionalPrice;

        assertEquals(expectedTotalPrice,basket.getTotalPrice(),0);
    }


    @Test
    public void getTotalPriceWithAfterRemovingPromotion() throws InvalidAttributesException {


        Map<Long, Double> specialPrice = new ConcurrentHashMap<>();
        specialPrice.put(3L,5.0);
        specialPrice.put(6L,3.0);

        ProductImpl newProduct = new ProductImpl("P1", 10);
        newProduct.addSpecialPrice(specialPrice);
        products.add(newProduct);
        products.add(newProduct);
        products.add(newProduct);

        basket.addProducts(products);

        //will remove promotion with empty promotion map for the latest prod
        Map<Long, Double> specialPriceUpdate = new ConcurrentHashMap<>();
        ProductImpl newProductWithUpdatedPromotion = new ProductImpl("P1", 10);
        newProductWithUpdatedPromotion.addSpecialPrice(specialPriceUpdate);

        basket.addProducts(Arrays.asList(newProductWithUpdatedPromotion));

        /**
         *  as there are 5 P1 products in the basket and promotion is removed
         */
        double expectedPromotionalPrice = 50;
        double expectedTotalPrice=0.0;

        for(Product tempProd : products)
        {
            if(!tempProd.equals(newProduct))
                expectedTotalPrice = expectedTotalPrice + tempProd.getPrice(1L);

        }

        expectedTotalPrice = expectedTotalPrice + expectedPromotionalPrice;

        assertEquals(expectedTotalPrice,basket.getTotalPrice(),0);
    }

}