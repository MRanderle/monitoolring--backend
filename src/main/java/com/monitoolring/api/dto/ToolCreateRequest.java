package com.monitoolring.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ToolCreateRequest(

        @NotBlank(message = "identificador é obrigatório")
        @Size(max = 64, message = "identificador deve ter no máximo 64 caracteres")
        String identificador,

        @NotBlank(message = "nome é obrigatório")
        @Size(max = 255, message = "nome deve ter no máximo 255 caracteres")
        String nome,

        @NotBlank(message = "categoria é obrigatória")
        @Size(max = 100, message = "categoria deve ter no máximo 100 caracteres")
        String categoria,

        @NotNull(message = "valorEstimado é obrigatório")
        @DecimalMin(value = "0.01", message = "valorEstimado deve ser maior que zero")
        BigDecimal valorEstimado,

        @NotBlank(message = "estadoConservacao é obrigatório")
        @Size(max = 255, message = "estadoConservacao deve ter no máximo 255 caracteres")
        String estadoConservacao
) {
}
