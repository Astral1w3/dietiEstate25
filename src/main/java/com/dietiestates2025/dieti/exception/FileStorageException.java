// in package com.dietiestates2025.dieti.exception;
package com.dietiestates2025.dieti.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Un errore 500 (Internal Server Error) è appropriato per problemi di I/O sul server.
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {
    
    public FileStorageException(String message) {
        super(message);
    }

    // Un costruttore che accetta anche la causa originale dell'errore (buona pratica)
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}