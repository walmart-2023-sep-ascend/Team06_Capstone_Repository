package com.capstone.CartAppService.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capstone.CartAppService.exception.CartNotFoundException;
import com.capstone.CartAppService.model.Cart;
import com.capstone.CartAppService.repository.CartRepository;

@Service
public class CartServiceImpl {
	@Autowired
	CartRepository crepo;

	int updatedQty;
	
	//ProductInventoryService piserv;
	int prodId;
	int quantity;
	
	public List<Cart> getAllCarts(){
		return crepo.findAll();
	}
	
	public Cart getCartbyId(int cid) throws CartNotFoundException{
		Cart c=null;
		Optional<Cart> opt=this.crepo.findBycartId(cid);
		if(opt.isPresent()) {
			c=opt.get();
			
		}
		else		
			throw new CartNotFoundException();
		return c;	
	}
	/*
	public List<Product> getProductsinCart(Cart c) throws ProductNotFoundException {
		Products products;
		List<Product> p=c.getProduct();
		for (Product product:p) {
			prodId=product.getProductId();
			quantity=product.getQuantity();	
			products=updateProduct(prodId, quantity);
			System.out.println("Updated product:"+ products);
		}
		return p;
	}
	public Products updateProduct(int pid, int quantity) throws ProductNotFoundException {
		Products p = null;
		Optional<Products> opt = irepo.findByproductId(pid);
		if (opt.isPresent()) {
			p = opt.get();
			updatedQty = p.getavailableQty() - quantity;
			p.setavailableQty(updatedQty);
			irepo.save(p);

		} else
			throw new ProductNotFoundException();
		return p;
	}*/
}
