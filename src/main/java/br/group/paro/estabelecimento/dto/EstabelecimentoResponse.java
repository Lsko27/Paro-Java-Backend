package br.group.paro.estabelecimento.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EstabelecimentoResponse(

        Long id,
        String nome,
        String descricao,
        String cep,
        String logradouro,
        String numero,
        String bairro,
        String cidade,
        String uf,
        String imagemUrl,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean ativo,
        LocalDateTime criadoEm,
        Long proprietarioId

) {}