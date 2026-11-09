package br.group.paro.usuario.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_PARO_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "usuario_seq"
    )
    @SequenceGenerator(
            name = "usuario_seq",
            sequenceName = "SEQ_PARO_USUARIO",
            allocationSize = 1
    )
    @Column(name = "ID_USUARIO")
    private Long id;

    @Column(name = "NM_USUARIO", nullable = false, length = 150)
    private String nome;

    @Column(
            name = "DS_EMAIL",
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    @Column(name = "DS_SENHA", nullable = false, length = 255)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_USUARIO", nullable = false, length = 20)
    private TipoUsuario tipoUsuario;

    @Column(name = "DT_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    protected Usuario() {
    }

    public Usuario(
            String nome,
            String email,
            String senhaHash,
            TipoUsuario tipoUsuario
    ) {
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.tipoUsuario = tipoUsuario;
    }

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}