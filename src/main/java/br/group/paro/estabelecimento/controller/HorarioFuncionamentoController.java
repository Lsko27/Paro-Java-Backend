package br.group.paro.estabelecimento.controller;

import br.group.paro.estabelecimento.dto.HorarioFuncionamentoRequest;
import br.group.paro.estabelecimento.dto.HorarioFuncionamentoResponse;
import br.group.paro.estabelecimento.service.HorarioFuncionamentoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estabelecimentos/{estabelecimentoId}/horarios")
@Validated
public class HorarioFuncionamentoController {

    private final HorarioFuncionamentoService horarioService;

    public HorarioFuncionamentoController(
            HorarioFuncionamentoService horarioService
    ) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<List<HorarioFuncionamentoResponse>> listar(
            @PathVariable Long estabelecimentoId
    ) {
        List<HorarioFuncionamentoResponse> horarios =
                horarioService.listar(estabelecimentoId);

        return ResponseEntity.ok(horarios);
    }

    @PutMapping
    public ResponseEntity<List<HorarioFuncionamentoResponse>> atualizar(
            @PathVariable Long estabelecimentoId,
            @RequestBody List<@Valid HorarioFuncionamentoRequest> requests,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        List<HorarioFuncionamentoResponse> horarios =
                horarioService.atualizar(
                        estabelecimentoId,
                        requests,
                        usuarioId
                );

        return ResponseEntity.ok(horarios);
    }
}