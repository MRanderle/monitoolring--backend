package com.monitoolring.api.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.monitoolring.api.domain.Tool;

@Repository
public class ToolRepository {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();

    public Tool save(Tool tool) {
        tools.put(tool.getId(), tool);
        return tool;
    }

    public Optional<Tool> findById(String id) {
        return Optional.ofNullable(tools.get(id));
    }

    public Optional<Tool> findByIdentificador(String identificador) {
        return tools.values().stream()
                .filter(tool -> tool.getIdentificador().equalsIgnoreCase(identificador))
                .findFirst();
    }

    public List<Tool> findAll() {
        return List.copyOf(tools.values());
    }
}
