package br.group.paro.usuario.dto;

import br.group.paro.usuario.entity.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroRequest(

        @NotBlank(message = "O nome é obrigatório")
        @Size(
                min = 3,
                max = 150,
                message = "O nome deve ter entre 3 e 150 caracteres"
        )
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Informe um e-mail válido")
        @Size(max = 150)
        String email,

        @Pattern(
                regexp = "^\\d{10,11}$",
                message = "O telefone deve conter 10 ou 11 dígitos com DDD"
        )
        String telefone,

        @NotBlank(message = "A senha é obrigatória")
        @Size(
                min = 8,
                max = 72,
                message = "A senha deve ter entre 8 e 72 caracteres"
        )
        String senha,

        @NotNull(message = "O tipo de usuário é obrigatório")
        TipoUsuario tipoUsuario

) {
}