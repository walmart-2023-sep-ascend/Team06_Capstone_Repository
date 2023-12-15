package com.capstone.InventoryUpdate.model;

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
public class Products {
	int productId;
	int availableQty;
	String productName;
	int productRetailPrice;
	
}
