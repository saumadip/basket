package com.ubs.supermarket.products;

import org.junit.Before;
import org.junit.Test;

import javax.naming.directory.InvalidAttributesException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class ProductImplTest {

    private Product product;

    @Before
    public void setUp() throws Exception
    {
         product = new ProductImpl("TestProduct",10);
    }

    /**
     * @throws InvalidAttributesException
     */
    @Test(expected = InvalidAttributesException.class)
    public void product_negetive_price() throws InvalidAttributesException {
        product = new ProductImpl("TestProduct",-1);
    }

    @Test
    public void getPrice_productCount0()
    {
        assertEquals(0,product.getPrice(0L),0);
    }

    @Test
    public void getPrice_productCount1()
    {
        assertEquals(10,product.getPrice(1L),0);
    }

    @Test
    public void getPrice_productCount10_AND_NoSpecialPrice()
    {
        assertEquals(100,product.getPrice(10L),0);
    }

    @Test
    public void getPrice_productCount10_WithSpecialPrice()
    {
        Map<Long, Double> specialPrice = new ConcurrentHashMap<>();
        specialPrice.put(3L,5.0);
        specialPrice.put(4L,4.0);
        specialPrice.put(6L,3.0);

        product.addSpecialPrice(specialPrice);

        assertEquals(15,product.getPrice(3L),0);
        assertEquals(16,product.getPrice(4L),0);
        assertEquals(18,product.getPrice(6L),0);
        assertEquals(0,product.getPrice(0L),0);
        assertEquals(0,product.getPrice(-1L),0);

        //4 products will come for price 4 and one will come for price 10
        assertEquals(26,product.getPrice(5L),0);
        // 6 will come for price 3 and one will be 10
        assertEquals(28,product.getPrice(7L),0);
        // 12 will come for price 3
        assertEquals(36,product.getPrice(12L),0);
        // 2 should come for 20
        assertEquals(20,product.getPrice(2L),0);
        // 2 should come for 20
        assertEquals(10,product.getPrice(1L),0);


    }

    @Test
    public void addSpecialPrice()
    {
        Map<Long, Double> specialPrice = new ConcurrentHashMap<>();
        specialPrice.put(3L,5.0);
        specialPrice.put(4L,4.0);
        specialPrice.put(6L,3.0);

        product.addSpecialPrice(specialPrice);

        Map<Long, Double> specialPrice2 = new ConcurrentHashMap<>();
        specialPrice.put(3L,2.0);
        specialPrice.put(4L,4.0);
        specialPrice.put(7L,5.0);

        Map<Long, Double> upDatedMap = product.addSpecialPrice(specialPrice);

        assertEquals(2.0,upDatedMap.get(3L),0);

        assertEquals(5,upDatedMap.get(7L),0);
    }
}