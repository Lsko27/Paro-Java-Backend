
package br.group.paro.auth.entity;

import br.group.paro.usuario.entity.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_PARO_RESET_SENHA")
public class PasswordResetToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reset_senha_seq"
    )
    @SequenceGenerator(
            name = "reset_senha_seq",
            sequenceName = "SEQ_PARO_RESET_SENHA",
            allocationSize = 1
    )
    @Column(name = "ID_RESET")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Column(
            name = "DS_TOKEN_HASH",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @Column(name = "DT_CRIACAO", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "DT_EXPIRACAO", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "DT_UTILIZACAO")
    private LocalDateTime utilizadoEm;

    protected PasswordResetToken() {}

    public PasswordResetToken(
            Usuario usuario,
            String tokenHash,
            LocalDateTime criadoEm,
            LocalDateTime expiraEm
    ) {
        this.usuario = usuario;
        this.tokenHash = tokenHash;
        this.criadoEm = criadoEm;
        this.expiraEm = expiraEm;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public LocalDateTime getUtilizadoEm() {
        return utilizadoEm;
    }
}
