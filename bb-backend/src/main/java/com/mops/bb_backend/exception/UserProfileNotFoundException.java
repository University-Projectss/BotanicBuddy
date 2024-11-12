package com.mops.bb_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such user profile")
public class UserProfileNotFoundException extends RuntimeException{
}
