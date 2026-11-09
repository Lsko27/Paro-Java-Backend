package br.group.paro.estabelecimentorecurso.dto;

public record EstabelecimentoRecursoResponse(

        Long id,
        Long estabelecimentoId,
        Long recursoId,
        String recursoNome,
        String recursoDescricao,
        Boolean disponivel,
        Boolean gratuito,
        String condicao

) {}