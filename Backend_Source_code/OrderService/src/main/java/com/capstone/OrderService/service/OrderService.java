package com.capstone.OrderService.service;

import java.util.List;

import com.capstone.OrderService.exception.OrderAlreadyExistsException;
import com.capstone.OrderService.exception.OrderNotFoundException;
import com.capstone.OrderService.model.Order;

public interface OrderService {
	List<Order> getAllOrders();
	Order getOrderbyId(int orderid) throws OrderNotFoundException;
	Order addOrder(Order order)throws OrderAlreadyExistsException;
	Order getOrderbycartId(int cartid)throws OrderNotFoundException;
	List<Order> getOrderbyuserId(int userid)throws OrderNotFoundException;
}
