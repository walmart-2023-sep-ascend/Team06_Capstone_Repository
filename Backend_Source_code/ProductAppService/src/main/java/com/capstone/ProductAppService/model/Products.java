package com.capstone.ProductAppService.model;

import org.springframework.data.mongodb.core.mapping.Document;


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
@Document(collection="Products")
public class Products {
	int productId;
	int availableQty;
	String productName;
	int productRetailPrice;
}
