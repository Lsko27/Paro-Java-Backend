package br.group.paro.estabelecimento.controller;

import br.group.paro.estabelecimento.dto.EstabelecimentoRequest;
import br.group.paro.estabelecimento.dto.EstabelecimentoResponse;
import br.group.paro.estabelecimento.service.EstabelecimentoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/estabelecimentos")
public class EstabelecimentoController {

    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(
            EstabelecimentoService estabelecimentoService
    ) {
        this.estabelecimentoService = estabelecimentoService;
    }

    @PostMapping
    public ResponseEntity<EstabelecimentoResponse> cadastrar(
            @Valid @RequestBody EstabelecimentoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        EstabelecimentoResponse response =
                estabelecimentoService.cadastrar(request, usuarioId);

        URI location = URI.create(
                "/api/v1/estabelecimentos/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<EstabelecimentoResponse>> listarAtivos() {
        return ResponseEntity.ok(
                estabelecimentoService.listarAtivos()
        );
    }

    @GetMapping("/meus")
    public ResponseEntity<List<EstabelecimentoResponse>> listarMeus(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                estabelecimentoService.listarMeus(usuarioId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponse> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                estabelecimentoService.buscarPorId(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstabelecimentoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstabelecimentoRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                estabelecimentoService.atualizar(
                        id,
                        request,
                        usuarioId
                )
        );
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<EstabelecimentoResponse> ativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                estabelecimentoService.ativar(id, usuarioId)
        );
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        estabelecimentoService.desativar(id, usuarioId);

        return ResponseEntity.noContent().build();
    }
}