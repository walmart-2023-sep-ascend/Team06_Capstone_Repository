package com.capstone.ShoppingFeedback.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.ShoppingFeedback.Service.FeedbackResponseServiceImpl;
import com.capstone.ShoppingFeedback.model.FeedbackResponse;	

@RestController
@CrossOrigin(origins="${capstone.feedbackform.url}")
public class FeedbackResponseController {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackResponseController.class);

	@Autowired
	FeedbackResponseServiceImpl resserv;
	ResponseEntity<?> resentity;
	
	@GetMapping("/allresponses")
	public ResponseEntity<?> getAllResponses() {
		List<FeedbackResponse> responses = resserv.getAllResponses();
		logger.info("All responses retrieved " + responses);
		resentity = new ResponseEntity<>(responses, HttpStatus.OK);
		return resentity;
	}

	@GetMapping("/responsebycartid/{cartId}")
	public ResponseEntity<?> getResponse(@PathVariable("cartId") int cartId) {
		FeedbackResponse response = resserv.getResponsebyId(cartId);
		logger.info("Response for " + cartId + " is: " + response);
		resentity = new ResponseEntity<>(response, HttpStatus.OK);
		return resentity;
	}

	@PostMapping("/addresponse")
	public ResponseEntity<?> addOrd(@RequestBody FeedbackResponse response) {		
		resserv.addResponse(response);
		logger.info("Response added successfully!!");
		resentity = new ResponseEntity<>("200: Response submitted successfully", HttpStatus.CREATED);
		return resentity;
	}
}
