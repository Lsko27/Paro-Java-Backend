package br.group.paro.impacto.controller;

import br.group.paro.impacto.dto.ImpactSummaryResponse;
import br.group.paro.impacto.service.ImpactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/impacto")
public class ImpactController {

    private final ImpactService impactService;

    public ImpactController(ImpactService impactService) {
        this.impactService = impactService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<ImpactSummaryResponse> obterResumo() {
        return ResponseEntity.ok(impactService.obterResumo());
    }
}