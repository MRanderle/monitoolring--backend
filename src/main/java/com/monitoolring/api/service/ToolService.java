package com.monitoolring.api.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.monitoolring.api.domain.Tool;
import com.monitoolring.api.dto.ToolCreateRequest;
import com.monitoolring.api.dto.ToolUpdateRequest;
import com.monitoolring.api.enums.ToolStatus;
import com.monitoolring.api.exception.DuplicateToolIdentifierException;
import com.monitoolring.api.exception.InvalidToolStatusTransitionException;
import com.monitoolring.api.exception.ToolNotFoundException;
import com.monitoolring.api.exception.ToolVersionConflictException;
import com.monitoolring.api.repository.ToolRepository;

@Service
public class ToolService {

    private final ToolRepository toolRepository;
    private final Object writeLock = new Object();

    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public Tool create(ToolCreateRequest request) {
        synchronized (writeLock) {
            assertIdentificadorAvailable(request.identificador());

            Tool tool = new Tool(
                    UUID.randomUUID().toString(),
                    request.identificador(),
                    request.nome(),
                    request.categoria(),
                    request.valorEstimado(),
                    request.estadoConservacao()
            );
            return toolRepository.save(tool);
        }
    }

    public List<Tool> findAll() {
        return toolRepository.findAll();
    }

    public Tool findById(String id) {
        return toolRepository.findById(id)
                .orElseThrow(() -> new ToolNotFoundException(id));
    }

    public Tool update(String id, ToolUpdateRequest request) {
        synchronized (writeLock) {
            Tool tool = findById(id);
            assertVersionMatches(tool, request.version());

            applyEditableFields(tool, request);
            applyInactivationIfRequested(tool, request.status());

            tool.touch();
            return toolRepository.save(tool);
        }
    }

    private void assertIdentificadorAvailable(String identificador) {
        toolRepository.findByIdentificador(identificador).ifPresent(existing -> {
            throw new DuplicateToolIdentifierException(identificador);
        });
    }

    private void assertVersionMatches(Tool tool, int expectedVersion) {
        if (tool.getVersion() != expectedVersion) {
            throw new ToolVersionConflictException(tool.getId(), expectedVersion, tool.getVersion());
        }
    }

    private void applyEditableFields(Tool tool, ToolUpdateRequest request) {
        Optional.ofNullable(request.nome()).ifPresent(tool::setNome);
        Optional.ofNullable(request.categoria()).ifPresent(tool::setCategoria);
        Optional.ofNullable(request.valorEstimado()).ifPresent(tool::setValorEstimado);
        Optional.ofNullable(request.estadoConservacao()).ifPresent(tool::setEstadoConservacao);
    }

    private void applyInactivationIfRequested(Tool tool, ToolStatus requestedStatus) {
        if (requestedStatus == null) {
            return;
        }
        if (requestedStatus != ToolStatus.INATIVA) {
            throw new InvalidToolStatusTransitionException(requestedStatus);
        }
        tool.setStatus(ToolStatus.INATIVA);
    }
}
