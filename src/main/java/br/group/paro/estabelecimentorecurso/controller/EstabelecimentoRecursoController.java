package br.group.paro.estabelecimentorecurso.controller;

import br.group.paro.estabelecimentorecurso.dto.EstabelecimentoRecursoRequest;
import br.group.paro.estabelecimentorecurso.dto.EstabelecimentoRecursoResponse;
import br.group.paro.estabelecimentorecurso.service.EstabelecimentoRecursoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estabelecimentos")
public class EstabelecimentoRecursoController {

    private final EstabelecimentoRecursoService estabelecimentoRecursoService;

    public EstabelecimentoRecursoController(
            EstabelecimentoRecursoService estabelecimentoRecursoService
    ) {
        this.estabelecimentoRecursoService = estabelecimentoRecursoService;
    }

    @PostMapping("/{estabelecimentoId}/recursos")
    public ResponseEntity<EstabelecimentoRecursoResponse> associar(
            @PathVariable Long estabelecimentoId,
            @Valid @RequestBody EstabelecimentoRecursoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        EstabelecimentoRecursoResponse response =
                estabelecimentoRecursoService.associar(
                        estabelecimentoId,
                        request,
                        usuarioId
                );

        URI location = URI.create(
                "/api/v1/estabelecimentos/recursos/" + response.id()
        );

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{estabelecimentoId}/recursos")
    public ResponseEntity<List<EstabelecimentoRecursoResponse>> listarDisponiveis(
            @PathVariable Long estabelecimentoId
    ) {
        return ResponseEntity.ok(
                estabelecimentoRecursoService.listarDisponiveis(
                        estabelecimentoId
                )
        );
    }

    @GetMapping("/{estabelecimentoId}/recursos/gerenciar")
    public ResponseEntity<List<EstabelecimentoRecursoResponse>> listarParaGerenciamento(
            @PathVariable Long estabelecimentoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                estabelecimentoRecursoService.listarParaGerenciamento(
                        estabelecimentoId,
                        usuarioId
                )
        );
    }

    @PutMapping("/recursos/{associacaoId}")
    public ResponseEntity<EstabelecimentoRecursoResponse> atualizar(
            @PathVariable Long associacaoId,
            @Valid @RequestBody EstabelecimentoRecursoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                estabelecimentoRecursoService.atualizar(
                        associacaoId,
                        request,
                        usuarioId
                )
        );
    }

    @DeleteMapping("/recursos/{associacaoId}")
    public ResponseEntity<Void> remover(
            @PathVariable Long associacaoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        estabelecimentoRecursoService.remover(
                associacaoId,
                usuarioId
        );

        return ResponseEntity.noContent().build();
    }
}