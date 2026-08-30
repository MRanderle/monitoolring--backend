package com.monitoolring.api.exception;

import com.monitoolring.api.enums.ToolStatus;

public class InvalidToolStatusTransitionException extends RuntimeException {

    public InvalidToolStatusTransitionException(ToolStatus requested) {
        super("Não é permitido definir o status '" + requested
                + "' diretamente por esta operação de edição.");
    }
}
