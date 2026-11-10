package br.group.paro.estabelecimento.entity;

import br.group.paro.usuario.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "T_PARO_AVALIACAO",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_PARO_AVALIACAO_USUARIO",
                columnNames = {"ID_ESTABELECIMENTO", "ID_USUARIO"}
        )
)
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AVALIACAO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "NR_NOTA", nullable = false)
    private Integer nota;

    @Column(name = "DS_COMENTARIO", length = 1000)
    private String comentario;

    @Column(name = "DT_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "DT_ATUALIZACAO")
    private LocalDateTime atualizadoEm;

    protected Avaliacao() {
    }

    public Avaliacao(
            Estabelecimento estabelecimento,
            Usuario usuario,
            Integer nota,
            String comentario
    ) {
        this.estabelecimento = estabelecimento;
        this.usuario = usuario;
        this.nota = nota;
        this.comentario = normalizarComentario(comentario);
    }

    @PrePersist
    protected void antesDePersistir() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void antesDeAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public void atualizarDados(Integer nota, String comentario) {
        this.nota = nota;
        this.comentario = normalizarComentario(comentario);
    }

    private String normalizarComentario(String comentario) {
        if (comentario == null || comentario.isBlank()) {
            return null;
        }

        return comentario.trim();
    }

    public Long getId() {
        return id;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}