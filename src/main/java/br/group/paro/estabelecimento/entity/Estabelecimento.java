package br.group.paro.estabelecimento.entity;

import br.group.paro.usuario.entity.Usuario;
import jakarta.persistence.*;
import org.hibernate.type.NumericBooleanConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_PARO_ESTABELECIMENTO")
public class Estabelecimento {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estabelecimento_seq"
    )
    @SequenceGenerator(
            name = "estabelecimento_seq",
            sequenceName = "SEQ_PARO_ESTABELECIMENTO",
            allocationSize = 1
    )
    @Column(name = "ID_ESTABELECIMENTO")
    private Long id;

    @Column(name = "NM_ESTABELECIMENTO", nullable = false, length = 150)
    private String nome;

    @Column(name = "DS_ESTABELECIMENTO", length = 1000)
    private String descricao;

    @Column(name = "NR_CEP", nullable = false, length = 8)
    private String cep;

    @Column(name = "DS_LOGRADOURO", nullable = false, length = 150)
    private String logradouro;

    @Column(name = "NR_ENDERECO", nullable = false, length = 20)
    private String numero;

    @Column(name = "NM_BAIRRO", nullable = false, length = 100)
    private String bairro;

    @Column(name = "NM_CIDADE", nullable = false, length = 100)
    private String cidade;

    @Column(name = "SG_UF", nullable = false, length = 2)
    private String uf;

    @Column(
            name = "NR_LATITUDE",
            nullable = false,
            precision = 10,
            scale = 7
    )
    private BigDecimal latitude;

    @Column(
            name = "NR_LONGITUDE",
            nullable = false,
            precision = 10,
            scale = 7
    )
    private BigDecimal longitude;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "ST_ATIVO", nullable = false)
    private Boolean ativo = true;

    @Column(
            name = "DT_CRIACAO",
            nullable = false,
            updatable = false
    )
    private LocalDateTime criadoEm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ID_USUARIO",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PARO_ESTAB_USUARIO")
    )
    private Usuario proprietario;

    protected Estabelecimento() {
    }

    public Estabelecimento(
            String nome,
            String descricao,
            String cep,
            String logradouro,
            String numero,
            String bairro,
            String cidade,
            String uf,
            BigDecimal latitude,
            BigDecimal longitude,
            Usuario proprietario
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.latitude = latitude;
        this.longitude = longitude;
        this.proprietario = proprietario;
        this.ativo = true;
    }

    @PrePersist
    protected void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
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

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Usuario getProprietario() {
        return proprietario;
    }

    public void atualizarDados(
            String nome,
            String descricao,
            String cep,
            String logradouro,
            String numero,
            String bairro,
            String cidade,
            String uf,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.nome = nome;
        this.descricao = descricao;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }
}