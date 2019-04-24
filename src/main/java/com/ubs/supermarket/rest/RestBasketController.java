package com.ubs.supermarket.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ubs.supermarket.basket.Basket;
import com.ubs.supermarket.basket.BasketImpl;
import com.ubs.supermarket.exception.DataNotFoundException;
import com.ubs.supermarket.products.Product;
import com.ubs.supermarket.service.BasketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Basket Rest controller
 * @author Saumadip Mazumder
 */
@RestController
public class RestBasketController
{

    @Autowired
    private BasketService basketService;

    private final Gson gson;

    public RestBasketController()
    {
        this.gson = new GsonBuilder().enableComplexMapKeySerialization()
                .setPrettyPrinting().create();
    }

    /**
     * Returns a newly created basket
     * @return
     */
    @PostMapping("/basket")
    public ResponseEntity<Basket> create()
    {

       Basket basket = new BasketImpl(new ConcurrentHashMap<>());

        basketService.addBasket(basket);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path(
                "/{basketId}").buildAndExpand(basket.getBasketID()).toUri();

        return ResponseEntity.created(location).build();

    }

    /**
     * Retrieval of basket details
     * @param basketId
     * @return Basket
     */
    @GetMapping("/basket/{basketId}")
    public ResponseEntity<String> basketDetails(@PathVariable UUID basketId)
    {
        if(basketId == null)
            throw new NullPointerException("Basket id cannot be null");

        if(!basketService.getBasketByID(basketId).isPresent())
            throw new DataNotFoundException("Basket ID not found");


        String json = gson.toJson(basketService.getBasketByID(basketId).get());
        return ResponseEntity.ok(json);
    }


    /**
     * Add product to the basket
     * @param basketId
     * @param product
     * @return
     * @throws JsonProcessingException
     */
    @PutMapping("/basket/{basketId}/addProducts")
    public ResponseEntity<String> addProducts(@PathVariable UUID basketId, @RequestBody ProductWrapper product) throws JsonProcessingException {
        if(basketId == null)
            throw new NullPointerException("Basket id cannot be null");

        if(!basketService.getBasketByID(basketId).isPresent())
            throw new DataNotFoundException("Basket ID not found");

        Basket basket = basketService.getBasketByID(basketId).get();

        Map<Product, Long> response = basket.addProducts(product.getProductList());


        String json = gson.toJson(response);
        return ResponseEntity.ok( json);

    }

    /**
     * Removes product from the basket
     * @param basketId
     * @param products
     * @return
     */
    @PutMapping("/basket/{basketId}/removeProducts")
    public ResponseEntity<String> removeProducts(@PathVariable UUID basketId, @RequestBody ProductWrapper products)
    {
        if(basketId == null)
            throw new NullPointerException("Basket id cannot be null");

        if(!basketService.getBasketByID(basketId).isPresent())
            throw new DataNotFoundException("Basket ID not found");

        Basket basket = basketService.getBasketByID(basketId).get();

        return ResponseEntity.ok(gson.toJson(basket.removeProducts(products.getProductList())));

    }


    /**
     * Total price rest API
     * @param basketId
     * @return
     */
    @GetMapping("/basket/{basketId}/total")
    ResponseEntity<String> checkTotalPrice(@PathVariable UUID basketId)
    {
        if(basketId == null)
            throw new NullPointerException("Basket id cannot be null");

        if(!basketService.getBasketByID(basketId).isPresent())
            throw new DataNotFoundException("Basket ID not found");

        Basket basket = basketService.getBasketByID(basketId).get();

        JsonObject obj = new JsonObject();
        obj.addProperty("total", basket.getTotalPrice());
        return ResponseEntity.ok(gson.toJson(obj));
    }


}
