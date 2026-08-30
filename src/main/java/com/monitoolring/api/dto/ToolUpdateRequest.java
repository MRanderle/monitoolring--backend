package com.monitoolring.api.dto;

import java.math.BigDecimal;

import com.monitoolring.api.enums.ToolStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ToolUpdateRequest(

        @NotNull(message = "version é obrigatório")
        Integer version,

        @Size(max = 255, message = "nome deve ter no máximo 255 caracteres")
        String nome,

        @Size(max = 100, message = "categoria deve ter no máximo 100 caracteres")
        String categoria,

        @DecimalMin(value = "0.01", message = "valorEstimado deve ser maior que zero")
        BigDecimal valorEstimado,

        @Size(max = 255, message = "estadoConservacao deve ter no máximo 255 caracteres")
        String estadoConservacao,

        ToolStatus status
) {
}
