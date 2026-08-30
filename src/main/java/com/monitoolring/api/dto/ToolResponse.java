package com.monitoolring.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.monitoolring.api.domain.Tool;
import com.monitoolring.api.enums.ToolStatus;

public record ToolResponse(
        String id,
        String identificador,
        String nome,
        String categoria,
        BigDecimal valorEstimado,
        String estadoConservacao,
        ToolStatus status,
        int version,
        Instant createdAt,
        Instant updatedAt
) {

    public static ToolResponse from(Tool tool) {
        return new ToolResponse(
                tool.getId(),
                tool.getIdentificador(),
                tool.getNome(),
                tool.getCategoria(),
                tool.getValorEstimado(),
                tool.getEstadoConservacao(),
                tool.getStatus(),
                tool.getVersion(),
                tool.getCreatedAt(),
                tool.getUpdatedAt()
        );
    }
}
