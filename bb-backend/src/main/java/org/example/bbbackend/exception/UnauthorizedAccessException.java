package org.example.bbbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "You do not have the necessary permissions to access this resource.")
public class UnauthorizedAccessException extends RuntimeException{
}
