package br.group.paro.estabelecimento.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record HorarioFuncionamentoRequest(

        @NotNull(message = "O dia da semana é obrigatório")
        @Min(value = 1, message = "O dia da semana deve estar entre 1 e 7")
        @Max(value = 7, message = "O dia da semana deve estar entre 1 e 7")
        Integer diaSemana,

        @Pattern(
                regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$",
                message = "A hora de abertura deve estar no formato HH:mm"
        )
        String horaAbertura,

        @Pattern(
                regexp = "^([01][0-9]|2[0-3]):[0-5][0-9]$",
                message = "A hora de fechamento deve estar no formato HH:mm"
        )
        String horaFechamento,

        @NotNull(message = "A situação de funcionamento é obrigatória")
        Boolean fechado

) {
}