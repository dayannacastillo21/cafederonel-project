package com.example.backend_cafedronel.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("El correo ya está registrado: " + email);
    }
}
