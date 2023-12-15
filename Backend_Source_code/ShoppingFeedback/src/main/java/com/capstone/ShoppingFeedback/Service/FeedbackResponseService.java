package com.capstone.ShoppingFeedback.Service;

import java.util.List;
import com.capstone.ShoppingFeedback.model.FeedbackResponse;

public interface FeedbackResponseService {
	List<FeedbackResponse> getAllResponses();
	FeedbackResponse getResponsebyId(int cartid);
	FeedbackResponse addResponse(FeedbackResponse res);
}
