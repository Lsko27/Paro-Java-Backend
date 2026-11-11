
package br.group.paro.favorito.controller;

import br.group.paro.favorito.dto.FavoritoResponse;
import br.group.paro.favorito.service.FavoritoService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public List<FavoritoResponse> listar(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return favoritoService.listar(usuarioId)
                .stream()
                .map(FavoritoResponse::fromEntity)
                .toList();
    }

    @PostMapping("/{estabelecimentoId}")
    public FavoritoResponse adicionar(
            @PathVariable Long estabelecimentoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return FavoritoResponse.fromEntity(
                favoritoService.adicionar(
                        usuarioId,
                        estabelecimentoId
                )
        );
    }

    @DeleteMapping("/{estabelecimentoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(
            @PathVariable Long estabelecimentoId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        favoritoService.remover(
                usuarioId,
                estabelecimentoId
        );
    }
}
