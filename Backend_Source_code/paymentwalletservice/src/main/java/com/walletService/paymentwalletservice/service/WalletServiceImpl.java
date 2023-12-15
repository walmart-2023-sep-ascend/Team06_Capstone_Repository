package com.walletService.paymentwalletservice.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.walletService.paymentwalletservice.email.EmailService;
import com.walletService.paymentwalletservice.exception.UserNotFoundException;
import com.walletService.paymentwalletservice.feign.EmailFeignClient;
import com.walletService.paymentwalletservice.feign.InventoryFeignClient;
import com.walletService.paymentwalletservice.feign.OrderFeignClient;
import com.walletService.paymentwalletservice.feign.ShippingCartFeignClient;
import com.walletService.paymentwalletservice.model.EmailDetails;
import com.walletService.paymentwalletservice.model.EventCodeLog;
import com.walletService.paymentwalletservice.model.InputData;
import com.walletService.paymentwalletservice.model.InventoryResponse;
import com.walletService.paymentwalletservice.model.Order;
import com.walletService.paymentwalletservice.model.Product;
import com.walletService.paymentwalletservice.model.ProductResponse;
import com.walletService.paymentwalletservice.model.Products;
import com.walletService.paymentwalletservice.model.ResponseData;
import com.walletService.paymentwalletservice.model.ShippingInput;
import com.walletService.paymentwalletservice.model.ShippngCart;
import com.walletService.paymentwalletservice.model.Users;
import com.walletService.paymentwalletservice.repository.UserRepository;
import com.walletService.paymentwalletservice.repository.WalletRepository;

@Service
public class WalletServiceImpl implements WalletService {

	Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	RestTemplate restTemplate;

	private final OrderFeignClient orderFeignClient;
	private final InventoryFeignClient inventoryFeignClient;
	private final EmailFeignClient emailFeignClient;
	private final ShippingCartFeignClient shippingCartFeignClient;
	

	@Autowired
	public WalletServiceImpl(OrderFeignClient orderFeignClient, InventoryFeignClient inventoryFeignClient,
			EmailFeignClient emailFeignClient, ShippingCartFeignClient shippingCartFeignClient) {

		this.orderFeignClient = orderFeignClient;
		this.inventoryFeignClient = inventoryFeignClient;
		this.emailFeignClient = emailFeignClient;
		this.shippingCartFeignClient = shippingCartFeignClient;
	}

	String currentDate = null;
	String deliveryDate = null;
	double totalAmount = 0;
	ShippingInput shippingInput;

	ResponseData responseData = new ResponseData();

	// To get current Date and time
	LocalDateTime currentDateTime = LocalDateTime.now();
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Define the desired format

	public Optional<Users> getUserInfo(int id) throws Exception {
		Optional<Users> user = null;
		try {
			logger.info("Log level: INFO : getting Customer Info");
			user = userRepository.findByuserId(id);
		} catch (Exception e) {
			logger.error("Log level: INFO : exception occured while getting Customer Info");
			throw new Exception(new UserNotFoundException());
		}

		return user;
	}

	public ResponseData validateWalletInfo(InputData inputData) throws Exception {
		logger.info("Log level: INFO : starting of ValidateWalletInfo ");
		ResponseData responseData = null;
		Optional<Users> user = null;
		try {
			user = getUserInfo(inputData.getUserId());
			if (user.get().getWallet() >= inputData.getTotalAmount()) {
				responseData = new ResponseData();
				responseData.setResponsecode("200");
				responseData.setUserId(inputData.getUserId());
				responseData.setWalletAmount(user.get().getWallet());
				responseData.setMessage("Wallet validation successful. The user has sufficient funds");
			} else {
				responseData = new ResponseData();
				responseData.setResponsecode("403");
				responseData.setUserId(inputData.getUserId());
				responseData.setWalletAmount(user.get().getWallet());
				responseData.setMessage("Wallet validation failed. The user does not have sufficient funds");
			}
		} catch (Exception e) {
			logger.error("Log level: INFO : exception occured while validating  Customer Wallet");
			throw new Exception(new UserNotFoundException());
		}

		return responseData;
	}

	public ResponseData validateWalletAuthentication(InputData inputData) throws Exception {
		ResponseData responseData = new ResponseData();
		try {

			EventCodeLog eventCodeLog = new EventCodeLog();
			eventCodeLog.setUserId(inputData.getUserId());
			int otp = otpGenerator();
			Optional<Users> user = getUserInfo(inputData.getUserId());
			emailService.sendEmail(user.get().getEmail(), otp, "Wallet Authentication OTP");
			eventCodeLog.setEventId(otp);
			// LocalDateTime date = LocalDateTime.now();
			eventCodeLog.setEventEntryTimeStamp(LocalDateTime.now());
			eventCodeLog.setEventExpiryTimeStamp(LocalDateTime.now().plusMinutes(2));
			walletRepository.save(eventCodeLog);
			responseData.setResponsecode("200");
			responseData.setUserId(inputData.getUserId());
			responseData.setMessage("OTP Sent successfully");
		} catch (Exception e) {
			logger.error("Log level: INFO : exception occured while validating  Customer Wallet");
			throw new Exception("Exception occured while Sending OTP to Email" + e.getMessage());
		}
		return responseData;

	}

	private static int otpGenerator() {
		int length = 5;
		String numbers = "1234567890";
		Random random = new Random();
		char[] otp = new char[length];

		for (int i = 0; i < length; i++) {
			otp[i] = numbers.charAt(random.nextInt(numbers.length()));
		}
		return Integer.parseInt(new String(otp));
	}

	public ResponseData validateOTP(InputData inputData) throws Exception {
		long noOfMinutes = 0;
		Optional<EventCodeLog> eventCodeLog = null;
		ResponseData responseData = new ResponseData();
		responseData.setUserId(inputData.getUserId());
		try {
			logger.info("Log level: INFO : getting Customer Info");
			eventCodeLog = walletRepository.findByeventId(inputData.getOtp());
			if (eventCodeLog.isPresent()) {
				EventCodeLog eventCode = eventCodeLog.get();
				LocalDateTime dateBefore = eventCode.getEventExpiryTimeStamp();
				noOfMinutes = dateBefore.until(LocalDateTime.now(), ChronoUnit.MINUTES);
				if (noOfMinutes <= 2) {
					responseData.setAuthenticated(true);
					responseData.setMessage("Authentication successful. User has been authenticated with OTP");
					responseData.setResponsecode("200");
				} else {
					responseData.setAuthenticated(false);
					responseData.setMessage("Authentication failed.");
					responseData.setResponsecode("403");
				}
			} else {
				responseData.setAuthenticated(false);
				responseData.setMessage("Incorrect OTP! Authentication failed.");
				responseData.setResponsecode("403");
			}

		} catch (Exception e) {
			logger.error("Log level: INFO : exception occured while authenticating user");
			throw new Exception("Exception occured while getting the Customer Info");
		}
		return responseData;
	}

	public ResponseData getPayableAmount(InputData inputData) throws Exception {
		double totalAmount = 0;
		ResponseData responseData = new ResponseData();
		try {
			shippingInput = new ShippingInput();
			shippingInput.setUserId(inputData.getUserId());
			shippingInput.setCartId(inputData.getCartId());
			ShippngCart shippngCart = shippingCartFeignClient.getShippingDetails(shippingInput);
			responseData.setUserId(inputData.getUserId());
			totalAmount = inputData.getAmount() + shippngCart.getShippingCost();
			responseData.setTotalAmount(totalAmount);
			responseData.setResponsecode("200");

		} catch (Exception e) {
			responseData.setResponsecode("403");
			logger.error("Log level: INFO : Exception occured while getting shippingCost Details !!");
			throw new Exception("Exception occured while getting shippingCost " + e.getMessage());
		}
		return responseData;
	}

	public ResponseData updateWallet(InputData inputData) throws Exception {

		ResponseData responseData = new ResponseData();
		try {
			responseData = getShippingDelivery(inputData);
			// update Wallet
			Optional<Users> userInfo = getUserInfo(inputData.getUserId());
			if (userInfo.isPresent()) {
				double wallet = userInfo.get().getWallet() - inputData.getTotalAmount();
				int result = userRepository.updateWallet(inputData.getUserId(), (float) wallet);
				if (result == 1) {
					responseData.setPhone(userInfo.get().getPhone());
					responseData.setResponsecode("200");
					responseData.setMessage("Payment Successful. Wallet amount has been deducted");
				} else {
					responseData.setResponsecode("403");
					responseData.setMessage("Unable to deduct Wallet amount!");
				}
			} else {
				responseData.setResponsecode("403");
				responseData.setMessage("User id not present");
			}
		} catch (Exception e) {
			logger.error("Log level: INFO : exception occured while while updating the Wallet Amount");
			throw new Exception("Exception occured while updating the Wallet Amount " + e.getMessage());
		}
		return responseData;
	}

	@Override
	public ResponseData getShippingDetails(InputData inputData) throws Exception {

		Optional<Users> user = null;
		ResponseData responseData = new ResponseData();
		try {
			responseData = getShippingDelivery(inputData);
			user = getUserInfo(inputData.getUserId());
			responseData.setPhone(user.get().getPhone());
			responseData.setResponsecode("200");
			responseData.setMessage("Payment will be done during delivery of the order");
			System.out.println(responseData);

		} catch (Exception e) {
			logger.error("Log level: INFO : exception occured while getting Shipping Details");
			throw new Exception("Exception occured while getting Shipping Details " + e.getMessage());
		}
		return responseData;
	}

	public ResponseData getShippingDelivery(InputData inputData) throws Exception {
		ResponseData responseData = new ResponseData();
		try {
			responseData.setUserId(inputData.getUserId());
			responseData.setCartId(inputData.getCartId());
			logger.error("Log level: INFO : Fetching ShippingCart details");

			shippingInput = new ShippingInput();
			shippingInput.setUserId(inputData.getUserId());
			shippingInput.setCartId(inputData.getCartId());
			ShippngCart shippngCart = shippingCartFeignClient.getShippingDetails(shippingInput);
			responseData.setDestinationOfShipping(shippngCart.getDestinationOfShipping());
			responseData.setTypeOfShipping(shippngCart.getTypeOfShipping());
			responseData.setTotalAmount(inputData.getTotalAmount());
			currentDate = String.valueOf(LocalDateTime.now());
			deliveryDate = String.valueOf(LocalDateTime.now().plusDays(shippngCart.getDeliveryDuration()));
			responseData.setDeliverydate(deliveryDate);

		} catch (Exception e) {
			responseData.setResponsecode("403");
			logger.error("Log level: INFO : exception occured while getting Shipping Details");
			throw new Exception("Exception occured while getting Shipping Details " + e.getMessage());
		}
		return responseData;
	}

	@Override
	public ResponseData orderUpdate(Order order) {

		ResponseEntity<InventoryResponse> inventorymsg = null;

		currentDate = String.valueOf(LocalDateTime.now());

		order.setDateOfOrder(currentDate);

		ResponseEntity<Order> orderresponse = orderFeignClient.addOrder(order);

		if (orderresponse.getStatusCode() == HttpStatus.CREATED) {
			System.out.println("Order created " + orderresponse);
			logger.info("Order Created:" + orderresponse);
			int cartid = orderresponse.getBody().getCartId();
			// Inventory update
			inventorymsg = inventoryUpdate(cartid);
			// Send email for order confirmation
			String email = sendEmailForOrderConfirmation(orderresponse.getBody(), inventorymsg.getBody());
			logger.info("Response from email service"+email);
			// Notify Order creation to downstream
			Order updateresponse=shippingCartFeignClient.notifyOrder(orderresponse.getBody());
			logger.info("Response from shippingCart service"+updateresponse);
			
			responseData.setOrderId(orderresponse.getBody().getOrderId());
			responseData.setCartId(orderresponse.getBody().getCartId());
			responseData.setDeliverydate(orderresponse.getBody().getDateOfDelivery());
			responseData.setTotalAmount(orderresponse.getBody().getAmount());
			responseData.setResponsecode("200");
			responseData.setMessage("Order Placed Successfully");
		} else {
			responseData.setResponsecode("403");
			responseData.setMessage("Unable to confirm order! Please try later");
		}
		return responseData;
	}

	@Override
	public ResponseEntity<InventoryResponse> inventoryUpdate(int cartid) {
		return inventoryFeignClient.updateInventory(cartid);
	}

	@Override
	public String sendEmailForOrderConfirmation(Order orderDetails, InventoryResponse inventoryResponse) {
		EmailDetails emailDetails = new EmailDetails();
		List<ProductResponse> productResponseList = new ArrayList<ProductResponse>();
		ProductResponse productResponse = null;

		emailDetails.setSubject("Order Confirmation");
		// Order Info
		emailDetails.setCartId(orderDetails.getCartId());
		emailDetails.setUserId(orderDetails.getUserId());
		emailDetails.setOrder_id(String.valueOf(orderDetails.getOrderId()));
		emailDetails.setDelivery_date(orderDetails.getDateOfDelivery());
		double shippingCost = 0;

		// Shipping cost details
		shippingInput = new ShippingInput();
		shippingInput.setUserId(orderDetails.getUserId());
		shippingInput.setCartId(inventoryResponse.getCartId());
		ShippngCart shippngCart = shippingCartFeignClient.getShippingDetails(shippingInput);
		shippingCost = shippngCart.getShippingCost();

		emailDetails.setShippingCost(String.valueOf(shippingCost));
		emailDetails.setTotalAmount(String.valueOf(orderDetails.getAmount()));
		// User Info
		Optional<Users> user = null;
		try {
			user = getUserInfo(orderDetails.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		emailDetails.setCustName(user.get().getUserName());
		emailDetails.setCustEmail(user.get().getEmail());

		// Product Info
		List<Product> product = inventoryResponse.getProduct();
		List<Products> products = inventoryResponse.getProducts();

		for (Product p : product) {
			productResponse = new ProductResponse();

			for (Products p1 : products) {
				if (p.getProductId() == p1.getProductId()) {
					productResponse.setProdName(p1.getProductName());
					productResponse.setProdRetialPrice(String.valueOf(p1.getProductRetailPrice()));
					productResponse.setProdQuantity(String.valueOf(p.getQuantity()));
					productResponse.setProdPrice(String.valueOf(p1.getProductRetailPrice() * p.getQuantity()));
					productResponseList.add(productResponse);
				}
			}

		}
		emailDetails.setProductResponse(productResponseList);
		return finalEmailForOrderConfirmation(emailDetails);

	}

	private String finalEmailForOrderConfirmation(EmailDetails emailDetails) {
		System.out.println(emailDetails);
		ResponseEntity<String> emailmsg = emailFeignClient.sendEmail(emailDetails);
		return emailmsg.getBody();
	}

}
