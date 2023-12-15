package com.capstone.OrderService.service;

import com.capstone.OrderService.model.Ordersequence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.Objects;

import static com.capstone.OrderService.model.Order.SEQUENCE_NAME;
import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SequenceGeneratorService {

	public static MongoOperations mongoOperations;

	@Autowired
	public SequenceGeneratorService(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public static Integer generateSequence(String seqName) {
		Ordersequence counter1 = mongoOperations.findAndModify(query(where("_id").is(seqName)),
				new Update().inc("orderId", 1), options().returnNew(true).upsert(true), Ordersequence.class);
		if (counter1 == null) {
			counter1 = new Ordersequence();
			counter1.setId(SEQUENCE_NAME);
			mongoOperations.insert(counter1);
		}
		System.out.println(counter1.getOrderId());
		return !Objects.isNull(counter1) ? counter1.getOrderId() : 1;
	}
}