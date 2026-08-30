package com.monitoolring.api.exception;

public class DuplicateToolIdentifierException extends RuntimeException {

    public DuplicateToolIdentifierException(String identificador) {
        super("Já existe uma ferramenta cadastrada com o identificador '" + identificador + "'.");
    }
}
