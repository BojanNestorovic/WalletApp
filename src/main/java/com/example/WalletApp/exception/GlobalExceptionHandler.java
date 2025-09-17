package com.example.WalletApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Globalni handler za izuzetke u aplikaciji
 * 
 * Ova klasa centralizovano rukuje svim izuzecima koji se mogu desiti
 * u aplikaciji i vraća odgovarajuće HTTP odgovore sa porukama grešaka.
 * Koristi @RestControllerAdvice anotaciju za globalno rukovanje izuzecima.
 * 
 * @author vuksta
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Rukuje izuzecima kada resurs nije pronađen
     * 
     * @param ex - izuzetak koji označava da resurs nije pronađen
     * @return HTTP 404 odgovor sa porukom greške
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Resource Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Rukuje izuzecima validacije
     * 
     * @param ex - izuzetak validacije
     * @return HTTP 400 odgovor sa porukom greške
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Rukuje izuzecima poslovne logike
     * 
     * @param ex - izuzetak poslovne logike
     * @return HTTP 400 odgovor sa porukom greške
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<Map<String, String>> handleBusinessLogicException(BusinessLogicException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Business Logic Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Rukuje izuzecima validacije Spring Boot-a
     * 
     * Ova metoda obrađuje greške validacije koje Spring Boot automatski
     * proverava na osnovu anotacija u DTO klasama.
     * 
     * @param ex - izuzetak validacije Spring Boot-a
     * @return HTTP 400 odgovor sa detaljnim greškama validacije
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        
        // Prikuplja sve greške validacije po poljima
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        response.put("error", "Validation Failed");
        response.put("message", "Invalid input data");
        response.put("details", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Rukuje izuzecima neispravnih argumenata
     * 
     * @param ex - izuzetak neispravnog argumenta
     * @return HTTP 400 odgovor sa porukom greške
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid Argument");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Rukuje opštim runtime izuzecima
     * 
     * @param ex - runtime izuzetak
     * @return HTTP 500 odgovor sa porukom greške
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Rukuje svim ostalim izuzecima
     * 
     * Ova metoda je "catch-all" handler koji hvata sve izuzetke
     * koji nisu obrađeni u prethodnim metodama.
     * 
     * @param ex - bilo koji izuzetak
     * @return HTTP 500 odgovor sa generičkom porukom greške
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Unexpected Error");
        error.put("message", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
