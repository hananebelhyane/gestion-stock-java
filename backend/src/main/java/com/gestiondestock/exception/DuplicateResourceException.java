
package com.gestiondestock.exception;

/**
 *
 * @author hp
 */

/**
 * Exception levée lorsqu'une ressource existe déjà (email ou téléphone en double)
 */
public class DuplicateResourceException extends RuntimeException {
    
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}