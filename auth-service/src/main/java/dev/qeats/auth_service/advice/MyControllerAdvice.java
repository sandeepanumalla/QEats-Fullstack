//package dev.qeats.auth_service.advice;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class MyControllerAdvice {
//
//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<?> handleException(ExpiredJwtException e) {
//        return ResponseEntity.status(401).body(e.getMessage());
//    }
//}
