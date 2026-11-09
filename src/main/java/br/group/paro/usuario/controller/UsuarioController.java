package br.group.paro.usuario.controller;

import br.group.paro.usuario.dto.UsuarioCadastroRequest;
import br.group.paro.usuario.dto.UsuarioResponse;
import br.group.paro.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioCadastroRequest request
    ) {
        UsuarioResponse usuario = usuarioService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }
}