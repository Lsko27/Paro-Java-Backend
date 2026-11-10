
package br.group.paro.estabelecimento.controller;

import br.group.paro.estabelecimento.dto.AvaliacaoRequest;
import br.group.paro.estabelecimento.dto.AvaliacaoResponse;
import br.group.paro.estabelecimento.dto.ResumoAvaliacoesResponse;
import br.group.paro.estabelecimento.service.AvaliacaoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estabelecimentos/{estabelecimentoId}/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoResponse>> listar(
            @PathVariable Long estabelecimentoId
    ) {
        List<AvaliacaoResponse> avaliacoes =
                avaliacaoService.listar(estabelecimentoId);

        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/resumo")
    public ResponseEntity<ResumoAvaliacoesResponse> buscarResumo(
            @PathVariable Long estabelecimentoId
    ) {
        ResumoAvaliacoesResponse resumo =
                avaliacaoService.buscarResumo(estabelecimentoId);

        return ResponseEntity.ok(resumo);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponse> cadastrar(
            @PathVariable Long estabelecimentoId,
            @Valid @RequestBody AvaliacaoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        AvaliacaoResponse avaliacao = avaliacaoService.cadastrar(
                estabelecimentoId,
                request,
                usuarioId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avaliacao);
    }

    @PutMapping("/minha")
    public ResponseEntity<AvaliacaoResponse> atualizarMinha(
            @PathVariable Long estabelecimentoId,
            @Valid @RequestBody AvaliacaoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        AvaliacaoResponse avaliacao = avaliacaoService.atualizarMinha(
                estabelecimentoId,
                request,
                usuarioId
        );

        return ResponseEntity.ok(avaliacao);
    }

    @DeleteMapping("/minha")
    public ResponseEntity<Void> excluirMinha(
            @PathVariable Long estabelecimentoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        avaliacaoService.excluirMinha(estabelecimentoId, usuarioId);

        return ResponseEntity.noContent().build();
    }
}
