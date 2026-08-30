package com.monitoolring.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monitoolring.api.domain.Tool;
import com.monitoolring.api.dto.ToolCreateRequest;
import com.monitoolring.api.dto.ToolResponse;
import com.monitoolring.api.dto.ToolUpdateRequest;
import com.monitoolring.api.service.ToolService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @PostMapping
    public ResponseEntity<ToolResponse> create(@Valid @RequestBody ToolCreateRequest request) {
        Tool tool = toolService.create(request);
        ToolResponse response = ToolResponse.from(tool);
        return ResponseEntity.created(URI.create("/api/tools/" + tool.getId())).body(response);
    }

    @GetMapping
    public List<ToolResponse> findAll() {
        return toolService.findAll().stream()
                .map(ToolResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ToolResponse findById(@PathVariable String id) {
        return ToolResponse.from(toolService.findById(id));
    }

    @PatchMapping("/{id}")
    public ToolResponse update(@PathVariable String id, @Valid @RequestBody ToolUpdateRequest request) {
        return ToolResponse.from(toolService.update(id, request));
    }
}
