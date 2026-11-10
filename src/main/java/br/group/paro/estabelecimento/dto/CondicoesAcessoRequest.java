package br.group.paro.estabelecimento.dto;

import jakarta.validation.constraints.Size;

public record CondicoesAcessoRequest(

        @Size(
                max = 1000,
                message = "As condições de acesso devem ter no máximo 1000 caracteres"
        )
        String condicoesAcesso

) {}