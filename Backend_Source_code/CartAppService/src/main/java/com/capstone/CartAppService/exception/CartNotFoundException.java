package com.capstone.CartAppService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.NOT_FOUND, reason="Cart id doesn't exist")
public class CartNotFoundException extends Exception {

}
