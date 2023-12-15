package com.capstone.ProductAppService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="Product id doesn't exist")
public class ProductNotFoundException extends Exception{

}
