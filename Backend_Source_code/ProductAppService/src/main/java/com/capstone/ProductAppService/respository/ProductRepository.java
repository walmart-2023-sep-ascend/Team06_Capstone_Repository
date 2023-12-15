package com.capstone.ProductAppService.respository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import com.capstone.ProductAppService.model.Products;

public interface ProductRepository extends MongoRepository<Products, Integer> {
	// Query to find product by its id
	@Query("{productId:?0}")
	Optional<Products> findByproductId(Integer productId);

	// Query to set product available quantity
	@Query("{productId:?0}")
	@Update("{'$set':{'availableQty':?1}}")
	int updateQuantity(int pid, int updatedQty);
}