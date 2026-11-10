
package br.group.paro.estabelecimento.dto;

import java.time.LocalDateTime;

public record AvaliacaoResponse(

        Long id,

        Long estabelecimentoId,

        Long usuarioId,

        String nomeUsuario,

        Integer nota,

        String comentario,

        LocalDateTime criadoEm,

        LocalDateTime atualizadoEm

) {
}
