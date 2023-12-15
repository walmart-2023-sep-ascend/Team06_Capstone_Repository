package com.capstone.ShoppingFeedback.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.capstone.ShoppingFeedback.Service.FeedbackResponseServiceImpl;
import com.capstone.ShoppingFeedback.model.FeedbackResponse;
import com.capstone.ShoppingFeedback.model.Response;
import com.capstone.ShoppingFeedback.repository.FeedbackResponseRepo;

@ExtendWith(MockitoExtension.class)
public class ShoppingFeedbackServiceTests {

	@InjectMocks
	FeedbackResponseServiceImpl feedbackservice;

	@Mock
	FeedbackResponseRepo feedbackrepo;

	@Test
	void testgetAllResponses() throws Exception {

		List<Response> response1 = new ArrayList<Response>();
		response1.addAll(Stream.of(new Response(4), new Response(4), new Response(4), new Response(4), new Response(4))
				.collect(Collectors.toList()));

		List<Response> response2 = new ArrayList<Response>();
		response2.addAll(Stream.of(new Response(5), new Response(5), new Response(5), new Response(5), new Response(5))
				.collect(Collectors.toList()));

		when(feedbackrepo.findAll())
				.thenReturn(Stream
						.of(new FeedbackResponse(1, 12345, response1, 4, "Good experience"),
								new FeedbackResponse(1, 12345, response2, 4, "Best experience"))
						.collect(Collectors.toList()));

		assertEquals(2, feedbackservice.getAllResponses().size());
	}

	@Test
	void testgetResponsebyId() throws Exception {

		List<Response> response = new ArrayList<Response>();
		response.addAll(Stream.of(new Response(4), new Response(4), new Response(4), new Response(4), new Response(4))
				.collect(Collectors.toList()));

		Optional<FeedbackResponse> fbresponse = Optional
				.of(new FeedbackResponse(1, 12345, response, 4, "Good experience"));

		when(feedbackrepo.findBycartId(any())).thenReturn(fbresponse);

		Optional<FeedbackResponse> feedbackresponse = Optional.of(feedbackservice.getResponsebyId(1));

		assertEquals(1, feedbackresponse.get().getCartId());
		assertEquals("Good experience", feedbackresponse.get().getComments());
		assertEquals(response.size(), feedbackresponse.get().getResponse().size());
		assertEquals(12345, feedbackresponse.get().getUserId());
		assertEquals(4, feedbackresponse.get().getAverageRating());

	}

	@Test
	void testaddResponse() throws Exception {

		List<Response> response = new ArrayList<Response>();
		response.addAll(Stream.of(new Response(3), new Response(3), new Response(3), new Response(3), new Response(3))
				.collect(Collectors.toList()));
		FeedbackResponse feedbackres = new FeedbackResponse(2, 123, response, 3, "Need improvement");

		when(feedbackrepo.save(feedbackres)).thenReturn(feedbackres);
		assertEquals(feedbackres, feedbackservice.addResponse(feedbackres));
	}

}
