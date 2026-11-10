package br.group.paro.busca.dto;

import java.math.BigDecimal;

public record BuscaEstabelecimentoResponse(
        Long id,
        String nome,
        String descricao,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String uf,
        BigDecimal latitude,
        BigDecimal longitude,
        double distanciaKm,
        String imagemUrl
) {}