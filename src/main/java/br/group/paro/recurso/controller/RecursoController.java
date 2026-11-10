package br.group.paro.recurso.controller;

import br.group.paro.recurso.dto.RecursoResponse;
import br.group.paro.recurso.service.RecursoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recursos")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    @GetMapping
    public ResponseEntity<List<RecursoResponse>> listarTodos() {
        return ResponseEntity.ok(recursoService.listarTodos());
    }
}