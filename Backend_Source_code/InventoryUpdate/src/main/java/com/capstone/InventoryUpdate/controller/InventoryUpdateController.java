package com.capstone.InventoryUpdate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.capstone.InventoryUpdate.model.InventoryResponse;
import com.capstone.InventoryUpdate.service.InventoryUpdateService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class InventoryUpdateController {


	@Autowired
	InventoryUpdateService inventoryupdate;
	ResponseEntity<?> resentity;

	@GetMapping("/inventoryupdate/{cartid}")
	public ResponseEntity<InventoryResponse> cartAPI(@PathVariable("cartid") int cartid) {
		return ResponseEntity.ok(inventoryupdate.cartProcess(cartid));
	}
}
