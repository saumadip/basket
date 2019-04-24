
package com.ubs.supermarket;

import com.ubs.supermarket.config.SwaggerConfig;
import com.ubs.supermarket.rest.RestBasketController;
import com.ubs.supermarket.service.BasketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = {SwaggerConfig.class, BasketService.class, RestBasketController.class})
public class SuperMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperMarketApplication.class, args);
	}

}

