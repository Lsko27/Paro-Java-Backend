package br.group.paro.usuario.controller;

import br.group.paro.usuario.dto.AtualizarMeuPerfilRequest;
import br.group.paro.usuario.dto.UsuarioResponse;
import br.group.paro.usuario.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
public class PerfilController {

    private final UsuarioService usuarioService;

    public PerfilController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarMeuPerfil(
            @Valid @RequestBody AtualizarMeuPerfilRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        UsuarioResponse usuarioAtualizado =
                usuarioService.atualizarMeuPerfil(
                        usuarioId,
                        request
                );

        return ResponseEntity.ok(usuarioAtualizado);
    }
}