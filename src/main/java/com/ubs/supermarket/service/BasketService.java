package com.ubs.supermarket.service;

import com.ubs.supermarket.basket.Basket;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class BasketService
{
    private final List<Basket> basketList = new CopyOnWriteArrayList<>();

    public Optional<Basket> getBasketByID(UUID basketID)
    {
        return basketList.stream().filter(basket -> basketID.equals(basket.getBasketID())).findAny();
    }

    public boolean addBasket(Basket basket)
    {
        return basketList.add(basket);
    }
}
