package com.capstone.OrderService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capstone.OrderService.exception.OrderAlreadyExistsException;
import com.capstone.OrderService.exception.OrderNotFoundException;
import com.capstone.OrderService.model.Order;
import com.capstone.OrderService.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	OrderRepository repo;

	// Get all Orders
	public List<Order> getAllOrders() {
		return repo.findAll();
	}

	// Get order by orderId
	public Order getOrderbyId(int orderid) throws OrderNotFoundException {
		Order order = null;
		Optional<Order> opt = this.repo.findByOrderId(orderid);
		if (opt.isPresent())
			order = opt.get();
		else
			throw new OrderNotFoundException();
		return order;
	}

	// Add order to database
	public Order addOrder(Order order) throws OrderAlreadyExistsException {
		Optional<Order> opt = this.repo.findById((int) order.getOrderId());
		if (opt.isPresent()) {
			throw new OrderAlreadyExistsException();
		}
		repo.save(order);
		return order;
	}

	// Get order by cart Id
	public Order getOrderbycartId(int cartid) throws OrderNotFoundException {
		Order order = null;
		Optional<Order> opt = this.repo.findOrderBycartId(cartid);
		if (opt.isPresent())
			order = opt.get();
		else
			throw new OrderNotFoundException();
		return order;
	}

	// Get orders by User Id
	public List<Order> getOrderbyuserId(int userid) throws OrderNotFoundException {
		List<Order> orders = null;
		Optional<List<Order>> opt = this.repo.findAllByuserId(userid);
		if (opt.isPresent()) {
			orders = opt.get();
		} else
			throw new OrderNotFoundException();
		return orders;
	}
}
