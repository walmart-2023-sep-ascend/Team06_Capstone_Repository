package com.capstone.ShoppingFeedback.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.ShoppingFeedback.model.FeedbackResponse;
import com.capstone.ShoppingFeedback.repository.FeedbackResponseRepo;

@Service
public class FeedbackResponseServiceImpl implements FeedbackResponseService {

	private static final Logger logger = LoggerFactory.getLogger(FeedbackResponseServiceImpl.class);

	@Autowired
	FeedbackResponseRepo resrepo;

	public List<FeedbackResponse> getAllResponses() {
		return resrepo.findAll();
	}

	public FeedbackResponse getResponsebyId(int cartid) {
		FeedbackResponse fs = null;
		Optional<FeedbackResponse> opt = this.resrepo.findBycartId(cartid);
		if (opt.isPresent()) {
			fs = opt.get();
			logger.info("Response found: " + fs);
		} else
			logger.info("Response not found for given Cart");
		return fs;
	}

	public FeedbackResponse addResponse(FeedbackResponse response) {
		resrepo.save(response);
		logger.info("Response added successfully" + response);
		return response;
	}
}
