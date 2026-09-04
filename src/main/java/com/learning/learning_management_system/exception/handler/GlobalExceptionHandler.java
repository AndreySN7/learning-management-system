package com.learning.learning_management_system.exception.handler;

import com.learning.learning_management_system.exception.DataValidateException;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(DataValidateException.class)
	public ResponseEntity<ErrorResponse> handleDataValidate(DataValidateException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidateException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldError().getDefaultMessage();
		ErrorResponse errorResponse = new ErrorResponse(message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleInternalServerError (Exception ex) {
		String message = "Внутренняя ошибка сервера";
		ErrorResponse errorResponse = new ErrorResponse(message);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
