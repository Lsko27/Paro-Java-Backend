package br.group.paro.estabelecimento.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EstabelecimentoRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
        String descricao,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(
                regexp = "^[0-9]{8}$",
                message = "O CEP deve conter exatamente 8 dígitos"
        )
        String cep,

        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 150)
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        @Size(max = 20)
        String numero,

        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100)
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100)
        String cidade,

        @NotBlank(message = "A UF é obrigatória")
        @Pattern(
                regexp = "^[A-Za-z]{2}$",
                message = "A UF deve conter duas letras"
        )
        String uf,

        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "Latitude inválida")
        @DecimalMax(value = "90.0", message = "Latitude inválida")
        @Digits(integer = 3, fraction = 7)
        BigDecimal latitude,

        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "Longitude inválida")
        @DecimalMax(value = "180.0", message = "Longitude inválida")
        @Digits(integer = 3, fraction = 7)
        BigDecimal longitude,

        @Size(max = 1000, message = "A URL da imagem deve ter no máximo 1000 caracteres")
        String imagemUrl


) {}