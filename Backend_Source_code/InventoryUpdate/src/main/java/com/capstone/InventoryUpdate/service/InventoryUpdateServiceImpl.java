package com.capstone.InventoryUpdate.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstone.InventoryUpdate.feign.CartFeignClient;
import com.capstone.InventoryUpdate.feign.ProductFeignClient;
import com.capstone.InventoryUpdate.model.Cart;
import com.capstone.InventoryUpdate.model.InventoryResponse;
import com.capstone.InventoryUpdate.model.Product;
import com.capstone.InventoryUpdate.model.Products;

@Service
public class InventoryUpdateServiceImpl implements InventoryUpdateService {

	private static final Logger logger = LoggerFactory.getLogger(InventoryUpdateServiceImpl.class);

	private final CartFeignClient cartFeignClient;
	private final ProductFeignClient productFeignClient;

	@Autowired
	public InventoryUpdateServiceImpl(CartFeignClient cartFeignClient, ProductFeignClient productFeignClient) {

		this.cartFeignClient = cartFeignClient;
		this.productFeignClient = productFeignClient;
	}

	// Local variables declaration
	int productid, quantity, availableQty, updatedQty;
	List<Products> listProducts;
	Products updatedProduct;
	InventoryResponse invresponse;

	public InventoryResponse cartProcess(int cartid) {
		invresponse = new InventoryResponse();
		listProducts = new ArrayList<Products>();

		invresponse.setCartId(cartid);

		// GET API call to get cart details
		Cart cart = cartFeignClient.getCartDetails(cartid);
		logger.info("Cart details: " + cart);

		List<Product> products = cart.getProduct();
		logger.info("Products inside cart: " + products);

		invresponse.setProduct(products);

		// Get and update quantity for each product in the list
		for (Product p : products) {

			productid = p.getProductId();
			quantity = p.getQuantity();
			logger.info("Product from Cart: " + p);
			Products product = productProcess(productid);
			listProducts.add(product);

		}
		logger.info("Updated product list: " + listProducts);
		invresponse.setProducts(listProducts);
		invresponse.setInventoryStatus("All Products inventory updated successfully");
		return invresponse;
	}

	public Products productProcess(int productid) {
		// GET API call to get product available quantity
		Products product = productFeignClient.getProductDetails(productid);
		logger.info("Current product: " + product);
		availableQty = product.getAvailableQty();

		// Calculate updated quantity
		updatedQty = availableQty - quantity;
		product.setAvailableQty(updatedQty);

		Products updatedProduct = productFeignClient.updateProduct(product);
		logger.info("Updated product: " + updatedProduct);
		return updatedProduct;
	}
}
