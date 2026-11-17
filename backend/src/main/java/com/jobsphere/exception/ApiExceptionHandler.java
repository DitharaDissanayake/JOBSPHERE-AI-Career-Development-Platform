package com.jobsphere.exception;
import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestControllerAdvice public class ApiExceptionHandler { @ExceptionHandler(Exception.class) public ResponseEntity<Map<String,Object>> handle(Exception e){String msg=e.getMessage();if(msg==null||msg.isBlank())msg="Something went wrong. Please try again.";return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success",false,"message",msg));} }
