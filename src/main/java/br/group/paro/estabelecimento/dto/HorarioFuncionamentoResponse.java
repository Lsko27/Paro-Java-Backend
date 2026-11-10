package br.group.paro.estabelecimento.dto;

public record HorarioFuncionamentoResponse(

        Long id,

        Long estabelecimentoId,

        Integer diaSemana,

        String horaAbertura,

        String horaFechamento,

        boolean fechado

) {
}