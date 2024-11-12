package com.mops.bb_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Account already has a profile")
public class AccountAlreadyHasProfile extends RuntimeException{
}
