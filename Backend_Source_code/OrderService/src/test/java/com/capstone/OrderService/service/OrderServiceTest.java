package com.capstone.OrderService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capstone.OrderService.model.Order;
import com.capstone.OrderService.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

	@InjectMocks
	OrderServiceImpl orderservice;

	@Mock
	OrderRepository orderrepo;
	
	@Test
	void testgetAllOrders() throws Exception {
		when(orderrepo.findAll())
				.thenReturn(Stream
						.of(new Order(12345,235,4132, "2023-11-28 21:21:07", 500.5f, "Digital Wallet",
								"Paid", "2023-11-30 21:21:07", "Placed"),
								new Order(12346,236,4133, "2023-11-27 21:21:07", 500.5f, "COD",
										"NotPaid", "2023-11-29 21:21:07", "Placed"))
						.collect(Collectors.toList()));
		assertEquals(2, orderservice.getAllOrders().size());
	}
	
	@Test
	void testgetOrderbyuserId() throws Exception{
		when(orderrepo.findAllByuserId(4132)).thenReturn(Optional.of(Stream
				.of(new Order(12345,235,4132, "2023-11-28 21:21:07", 500.5f, "Digital Wallet",
						"Paid", "2023-11-30 21:21:07", "Placed"),
						new Order(12346,236,4132, "2023-11-27 21:21:07", 1500.5f, "COD",
								"NotPaid", "2023-11-29 21:21:07", "Placed"))
				.collect(Collectors.toList())));
		assertEquals(2, orderservice.getOrderbyuserId(4132).size());		
	}
	
	@Test
	void testgetOrderbycartId() throws Exception{
		Optional<Order> order = Optional
				.of(new Order(12345,235,4132, "2023-11-28 21:21:07", 500.5f, "Digital Wallet",
						"Paid", "2023-11-30 21:21:07", "Placed"));

		when(orderrepo.findOrderBycartId(235)).thenReturn(order);

		Optional<Order> orderresponse = Optional.of(orderservice.getOrderbycartId(235));

		assertEquals(12345, orderresponse.get().getOrderId());
		assertEquals(235, orderresponse.get().getCartId());
		assertEquals(4132, orderresponse.get().getUserId());
		assertEquals("2023-11-28 21:21:07", orderresponse.get().getDateOfOrder());
		assertEquals(500.5f, orderresponse.get().getAmount());
		assertEquals("Digital Wallet", orderresponse.get().getModeOfPayment());		
		assertEquals("Paid", orderresponse.get().getPaymentStatus());
		assertEquals("2023-11-30 21:21:07", orderresponse.get().getDateOfDelivery());
		assertEquals("Placed", orderresponse.get().getStatusOfOrder());
	}
	
	@Test
	void testgetOrderbyorderId() throws Exception{
		Optional<Order> order = Optional
				.of(new Order(12346,235,4132, "2023-11-28 21:21:07", 500.5f, "COD",
						"NotPaid", "2023-11-30 21:21:07", "Placed"));

		when(orderrepo.findByOrderId(12346)).thenReturn(order);

		Optional<Order> orderresponse = Optional.of(orderservice.getOrderbyId(12346));

		assertEquals(12346, orderresponse.get().getOrderId());
		assertEquals(235, orderresponse.get().getCartId());
		assertEquals(4132, orderresponse.get().getUserId());
		assertEquals("2023-11-28 21:21:07", orderresponse.get().getDateOfOrder());
		assertEquals(500.5f, orderresponse.get().getAmount());
		assertEquals("COD", orderresponse.get().getModeOfPayment());		
		assertEquals("NotPaid", orderresponse.get().getPaymentStatus());
		assertEquals("2023-11-30 21:21:07", orderresponse.get().getDateOfDelivery());
		assertEquals("Placed", orderresponse.get().getStatusOfOrder());
	}
	
	@Test
	void testaddOrder() throws Exception{
		Order order= new Order(12346,235,4132, "2023-11-28 21:21:07", 500.5f, "COD",
				"NotPaid", "2023-11-30 21:21:07", "Placed");

		when(orderrepo.save(order)).thenReturn(order);
		assertEquals(order, orderservice.addOrder(order));
	}
	
}
