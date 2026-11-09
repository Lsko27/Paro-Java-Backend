package br.group.paro.estabelecimentorecurso.entity;

import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.recurso.entity.Recurso;

import jakarta.persistence.*;

import org.hibernate.type.NumericBooleanConverter;

@Entity
@Table(
        name = "T_PARO_ESTAB_RECURSO",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_PARO_ESTAB_RECURSO",
                columnNames = {
                        "ID_ESTABELECIMENTO",
                        "ID_RECURSO"
                }
        )
)
public class EstabelecimentoRecurso {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estab_recurso_seq"
    )
    @SequenceGenerator(
            name = "estab_recurso_seq",
            sequenceName = "SEQ_PARO_ESTAB_RECURSO",
            allocationSize = 1
    )
    @Column(name = "ID_ESTAB_RECURSO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ID_ESTABELECIMENTO",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_PARO_ER_ESTAB"
            )
    )
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ID_RECURSO",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_PARO_ER_RECURSO"
            )
    )
    private Recurso recurso;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ST_DISPONIVEL", nullable = false)
    private Boolean disponivel = true;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ST_GRATUITO", nullable = false)
    private Boolean gratuito = true;

    @Column(name = "DS_CONDICAO", length = 500)
    private String condicao;

    protected EstabelecimentoRecurso() {
    }

    public EstabelecimentoRecurso(
            Estabelecimento estabelecimento,
            Recurso recurso,
            Boolean disponivel,
            Boolean gratuito,
            String condicao
    ) {
        this.estabelecimento = estabelecimento;
        this.recurso = recurso;
        this.disponivel = disponivel;
        this.gratuito = gratuito;
        this.condicao = condicao;
    }

    public Long getId() {
        return id;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public Boolean getGratuito() {
        return gratuito;
    }

    public String getCondicao() {
        return condicao;
    }

    public void atualizarDados(
            Boolean disponivel,
            Boolean gratuito,
            String condicao
    ) {
        this.disponivel = disponivel;
        this.gratuito = gratuito;
        this.condicao = condicao;
    }
}