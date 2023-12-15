package com.capstone.OrderService.controller;

import java.util.List;

import com.capstone.OrderService.service.SequenceGeneratorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.capstone.OrderService.exception.OrderAlreadyExistsException;
import com.capstone.OrderService.exception.OrderNotFoundException;
import com.capstone.OrderService.model.Order;
import com.capstone.OrderService.service.OrderService;

@RestController
public class OrderController {

	private static final String ORDER_SERVICE = "OrderService";
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	OrderService orderservice;
	ResponseEntity<?> resentity;

	@Autowired
	RestTemplate resttemplate;

	@GetMapping("/orders")
	public ResponseEntity<?> getAllOrders() {
		List<Order> orderlist = orderservice.getAllOrders();
		if (orderlist.isEmpty())
			return new ResponseEntity<>("No orders present", HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>(orderlist, HttpStatus.OK);
	}

	@GetMapping("/orderbyuserid/{userid}")
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<?> getOrdersbyUserId(@PathVariable("userid") int userid) throws OrderNotFoundException {
		List<Order> orderlist = orderservice.getOrderbyuserId(userid);
		if (orderlist.isEmpty())
			return new ResponseEntity<>("Order not found for given userId: " + userid, HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>(orderlist, HttpStatus.OK);
	}

	@GetMapping("/orderbyid/{orderid}")
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<?> getOrder(@PathVariable("orderid") int orderid) throws OrderNotFoundException {
		Order order = orderservice.getOrderbyId(orderid);
		if (order == null)
			return new ResponseEntity<>("Order not found for given orderId: " + orderid, HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@GetMapping("/orderbycartid/{cartid}")
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<?> getOrderbyCartId(@PathVariable("cartid") int cartid) throws OrderNotFoundException {
		Order order = orderservice.getOrderbycartId(cartid);
		if (order == null)
			return new ResponseEntity<>("Order not found for given cartId: " + cartid, HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>(order, HttpStatus.OK);
	}

	@PostMapping("/addorder")
	@ExceptionHandler(OrderAlreadyExistsException.class)
	public ResponseEntity<?> addOrd(@RequestBody Order ord) throws OrderAlreadyExistsException {
		try {
			resttemplate = new RestTemplate();
			ord.setOrderId(SequenceGeneratorService.generateSequence(Order.SEQUENCE_NAME));
			ord.setStatusOfOrder("Placed");
			orderservice.addOrder(ord);
			resentity = new ResponseEntity<>(ord, HttpStatus.CREATED);
		} catch (OrderAlreadyExistsException e) {
			throw new OrderAlreadyExistsException();
		} catch (Exception e) {
			logger.info("Exception: ", e);
			resentity = new ResponseEntity<>("Adding Failed,pls try later", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resentity;
	}
}