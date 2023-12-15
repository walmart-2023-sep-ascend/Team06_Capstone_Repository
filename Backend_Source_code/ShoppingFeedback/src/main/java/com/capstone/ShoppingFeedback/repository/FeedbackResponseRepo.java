package com.capstone.ShoppingFeedback.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.capstone.ShoppingFeedback.model.FeedbackResponse;

public interface FeedbackResponseRepo extends MongoRepository<FeedbackResponse,Integer>{
	
	@Query("{cartId:?0}")
    Optional<FeedbackResponse> findBycartId(Integer cartId);
    
}
