package com.moneylion.interview.spring.webserver.exception;

import com.networknt.schema.JsonSchemaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     *  handle NoSuchElementFoundException
     * @param exception
     * @param request
     * @return 404 Not Found
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNoSuchElementFoundException(NoSuchElementException exception, WebRequest request) {
        log.error("Failed to find the requested element", exception);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    /**
     * handle JsonSchemaException
     * @param exception
     * @param request
     * @return 400 Bad Request
     */
    @ExceptionHandler(JsonSchemaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleJsonSchemaException(JsonSchemaException exception, WebRequest request) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    /**
     * handle IllegalArgumentException
     * @param exception
     * @param request
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        log.error("Received illegal argument");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    /**
     * handle unexpected exception
     * @param exception
     * @param request
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        log.error("Unknown error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error occurred");
    }


    /**
     * handle internal exception
     * @param ex
     * @param body
     * @param headers
     * @param status
     * @param request
     * @return response with status code
     */
    @Override
    public ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return ResponseEntity.status(status).body(ex.getMessage());
    }
}
