package com.ubs.supermarket.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ubs.supermarket.basket.BasketImpl;
import com.ubs.supermarket.products.Product;

import java.io.IOException;
import java.util.Map;

public class ProductSerializer extends JsonSerializer<BasketImpl>
{

    @Override
    public void serialize(BasketImpl basket, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException
    {
        Map<Product, Long> allProducts = basket.getAllProducts();

        for(Map.Entry<Product, Long> data:allProducts.entrySet()) {
            Product product = data.getKey();
            jsonGenerator.writeStringField("name", product.getName());
            jsonGenerator.writeNumberField("price", product.getPrice(1L));
            jsonGenerator.writeFieldName("specialPriceMap");
            jsonGenerator.writeObject(product.getSpecialPriceMap());
            jsonGenerator.writeNumberField("count",data.getValue());
            jsonGenerator.close();
        }

    }
}
