package org.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Simple global error handler for invalid path. Intended to replace the
 * default WhiteLabel page and provide usage instructions for the app.
 */
@ControllerAdvice()
public class GlobalExceptionController {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<NotFoundRecord> noHandlerException(NoHandlerFoundException ex) {
        String msg = String.format("404 Not Found for %s", ex.getRequestURL());
        String usage = "Brandon's App correct usage: https://{hostname}.com/github/user/{username}";
        return new ResponseEntity<>(new NotFoundRecord(msg, usage), HttpStatus.NOT_FOUND);
    }


    public record NotFoundRecord (String error, String usage) {}
}
