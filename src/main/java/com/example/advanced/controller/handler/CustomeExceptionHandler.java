package com.example.advanced.controller.handler;

import com.example.advanced.controller.response.ResponseDto;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import software.amazon.awssdk.services.servicequotas.model.IllegalArgumentException;

@RestControllerAdvice
public class CustomeExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseDto<?> handleMethodArgExceptions(MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult()
            .getAllErrors()
            .get(0)
            .getDefaultMessage();

    return ResponseDto.fail("BAD_REQUEST", errorMessage);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseDto<?> handleIllegalArgExceptions(IllegalArgumentException exception) {
    return ResponseDto.fail(Integer.toString(exception.statusCode()), exception.getMessage());
  }


}







