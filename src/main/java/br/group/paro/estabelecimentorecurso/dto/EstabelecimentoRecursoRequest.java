package br.group.paro.estabelecimentorecurso.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EstabelecimentoRecursoRequest(

        @NotNull(message = "O recurso é obrigatório")
        @Positive(message = "O ID do recurso deve ser positivo")
        Long recursoId,

        @NotNull(message = "A disponibilidade é obrigatória")
        Boolean disponivel,

        @NotNull(message = "Informe se o recurso é gratuito")
        Boolean gratuito,

        @Size(
                max = 500,
                message = "A condição deve ter no máximo 500 caracteres"
        )
        String condicao

) {}