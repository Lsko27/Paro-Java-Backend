
package br.group.paro.estabelecimento.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(
        name = "T_PARO_HORARIO",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_PARO_HORARIO_DIA",
                columnNames = {"ID_ESTABELECIMENTO", "NR_DIA_SEMANA"}
        )
)
public class HorarioFuncionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HORARIO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESTABELECIMENTO", nullable = false)
    private Estabelecimento estabelecimento;

    @Column(name = "NR_DIA_SEMANA", nullable = false)
    private Integer diaSemana;

    @Column(name = "HR_ABERTURA", length = 5)
    private String horaAbertura;

    @Column(name = "HR_FECHAMENTO", length = 5)
    private String horaFechamento;

    @Column(name = "ST_FECHADO", nullable = false)
    private Integer fechado;

    protected HorarioFuncionamento() {
    }

    public HorarioFuncionamento(
            Estabelecimento estabelecimento,
            Integer diaSemana,
            LocalTime horaAbertura,
            LocalTime horaFechamento,
            boolean fechado
    ) {
        this.estabelecimento = estabelecimento;
        this.diaSemana = diaSemana;
        atualizarDados(horaAbertura, horaFechamento, fechado);
    }

    public void atualizarDados(
            LocalTime horaAbertura,
            LocalTime horaFechamento,
            boolean fechado
    ) {
        this.fechado = fechado ? 1 : 0;
        this.horaAbertura = fechado || horaAbertura == null
                ? null
                : horaAbertura.toString();

        this.horaFechamento = fechado || horaFechamento == null
                ? null
                : horaFechamento.toString();
    }

    public Long getId() {
        return id;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraAbertura() {
        return horaAbertura == null
                ? null
                : LocalTime.parse(horaAbertura);
    }

    public LocalTime getHoraFechamento() {
        return horaFechamento == null
                ? null
                : LocalTime.parse(horaFechamento);
    }

    public boolean isFechado() {
        return fechado == 1;
    }
}
