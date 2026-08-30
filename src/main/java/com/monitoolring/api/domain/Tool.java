package com.monitoolring.api.domain;

import java.math.BigDecimal;
import java.time.Instant;

import com.monitoolring.api.enums.ToolStatus;

public class Tool {

    private final String id;
    private String identificador;
    private String nome;
    private String categoria;
    private BigDecimal valorEstimado;
    private String estadoConservacao;
    private ToolStatus status;
    private int version;
    private final Instant createdAt;
    private Instant updatedAt;

    public Tool(String id, String identificador, String nome, String categoria,
                BigDecimal valorEstimado, String estadoConservacao) {
        this.id = id;
        this.identificador = identificador;
        this.nome = nome;
        this.categoria = categoria;
        this.valorEstimado = valorEstimado;
        this.estadoConservacao = estadoConservacao;
        this.status = ToolStatus.DISPONIVEL;
        this.version = 0;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getEstadoConservacao() {
        return estadoConservacao;
    }

    public void setEstadoConservacao(String estadoConservacao) {
        this.estadoConservacao = estadoConservacao;
    }

    public ToolStatus getStatus() {
        return status;
    }

    public void setStatus(ToolStatus status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void touch() {
        this.version++;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
