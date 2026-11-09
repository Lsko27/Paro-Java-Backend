package br.group.paro.auth.dto;

import br.group.paro.usuario.dto.UsuarioResponse;

public record LoginResponse(
        String message,
        UsuarioResponse usuario
) {}