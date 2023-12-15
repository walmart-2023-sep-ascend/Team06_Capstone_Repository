package com.capstone.OrderService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.capstone.OrderService.model.Order;

public interface OrderRepository extends MongoRepository<Order, Integer> {
	// Query to find by order id
	@Query("{orderId:?0}")
	Optional<Order> findByOrderId(Integer orderId);

	// Query to find by order id
	@Query("{cartId:?0}")
	Optional<Order> findOrderBycartId(int cartid);

	// Query to find by order id
	@Query("{userId:?0}")
	Optional<List<Order>> findAllByuserId(int userid);
}
