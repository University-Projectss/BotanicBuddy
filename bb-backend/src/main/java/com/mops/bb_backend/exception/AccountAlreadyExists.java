package com.mops.bb_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "An account with the provided email already exists")
public class AccountAlreadyExists extends RuntimeException{
}

