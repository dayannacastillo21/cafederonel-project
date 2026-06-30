package com.example.backend_cafedronel.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales incorrectas o usuario inactivo.");
    }
}
