package com.capstone.CartAppService.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.capstone.CartAppService.model.Cart;

public interface CartRepository extends MongoRepository<Cart,Integer>{
	@Query("{cartId:?0}")
    Optional<Cart> findBycartId(Integer cartId);
}
