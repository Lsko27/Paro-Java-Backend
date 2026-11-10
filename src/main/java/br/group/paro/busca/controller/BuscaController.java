package br.group.paro.busca.controller;

import br.group.paro.busca.dto.BuscaEstabelecimentoResponse;
import br.group.paro.busca.service.BuscaService;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/estabelecimentos")
public class BuscaController {

    private final BuscaService buscaService;

    public BuscaController(BuscaService buscaService) {
        this.buscaService = buscaService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<BuscaEstabelecimentoResponse>> buscar(

            @RequestParam
            @DecimalMin("-90.0")
            @DecimalMax("90.0")
            BigDecimal latitude,

            @RequestParam
            @DecimalMin("-180.0")
            @DecimalMax("180.0")
            BigDecimal longitude,

            @RequestParam(defaultValue = "10")
            @DecimalMin("0.1")
            @DecimalMax("50.0")
            double raioKm,

            @RequestParam(required = false)
            @Positive
            Long recursoId

    ) {
        return ResponseEntity.ok(
                buscaService.buscar(
                        latitude,
                        longitude,
                        raioKm,
                        recursoId
                )
        );
    }
}