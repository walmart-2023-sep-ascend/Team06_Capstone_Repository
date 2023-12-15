package com.capstone.InventoryUpdate.service;

import com.capstone.InventoryUpdate.model.InventoryResponse;
import com.capstone.InventoryUpdate.model.Products;

public interface InventoryUpdateService {
	InventoryResponse cartProcess(int cartid);
	Products productProcess(int productid);
}
