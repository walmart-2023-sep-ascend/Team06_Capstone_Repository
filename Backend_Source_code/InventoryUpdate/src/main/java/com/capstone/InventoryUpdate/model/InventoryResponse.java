package com.capstone.InventoryUpdate.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InventoryResponse {

	private int cartId;
	private List<Product> product;
	private List<Products> products;
	private String inventoryStatus;
	
}
