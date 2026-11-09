package br.group.paro.usuario.dto;

import br.group.paro.usuario.entity.TipoUsuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        TipoUsuario tipoUsuario,
        LocalDateTime criadoEm
) {}