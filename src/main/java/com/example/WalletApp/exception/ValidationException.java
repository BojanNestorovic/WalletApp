package com.example.WalletApp.exception;

/**
 * Izuzetak koji se baca kada validacija podataka ne prođe
 * 
 * Ova klasa predstavlja izuzetak koji se koristi za označavanje
 * grešaka u validaciji podataka. Nasleđuje RuntimeException
 * što znači da je unchecked izuzetak.
 * 
 * @author vuksta
 * @version 1.0
 */
public class ValidationException extends RuntimeException {
    
    /**
     * Konstruktor sa porukom greške
     * 
     * @param message - poruka koja opisuje grešku u validaciji
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Konstruktor sa porukom greške i uzrokom
     * 
     * @param message - poruka koja opisuje grešku u validaciji
     * @param cause - izuzetak koji je uzrokovao ovu grešku
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
