
package br.group.paro.favorito.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "T_PARO_FAVORITO",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_PARO_FAVORITO",
                columnNames = {
                        "ID_USUARIO",
                        "ID_ESTABELECIMENTO"
                }
        )
)
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FAVORITO")
    private Long id;

    @Column(name = "ID_USUARIO", nullable = false)
    private Long usuarioId;

    @Column(name = "ID_ESTABELECIMENTO", nullable = false)
    private Long estabelecimentoId;

    protected Favorito() {
    }

    public Favorito(Long usuarioId, Long estabelecimentoId) {
        this.usuarioId = usuarioId;
        this.estabelecimentoId = estabelecimentoId;
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getEstabelecimentoId() {
        return estabelecimentoId;
    }
}
