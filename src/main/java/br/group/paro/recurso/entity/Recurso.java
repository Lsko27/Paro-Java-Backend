package br.group.paro.recurso.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "T_PARO_RECURSO")
public class Recurso {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "recurso_seq"
    )
    @SequenceGenerator(
            name = "recurso_seq",
            sequenceName = "SEQ_PARO_RECURSO",
            allocationSize = 1
    )
    @Column(name = "ID_RECURSO")
    private Long id;

    @Column(
            name = "NM_RECURSO",
            nullable = false,
            unique = true,
            length = 100
    )
    private String nome;

    @Column(name = "DS_RECURSO", length = 500)
    private String descricao;

    protected Recurso() {
    }

    public Recurso(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void atualizarDados(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
}