
package br.group.paro.estabelecimento.repository;

import br.group.paro.estabelecimento.entity.HorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioFuncionamentoRepository
        extends JpaRepository<HorarioFuncionamento, Long> {

    List<HorarioFuncionamento>
    findByEstabelecimentoIdOrderByDiaSemanaAsc(Long estabelecimentoId);

    Optional<HorarioFuncionamento>
    findByEstabelecimentoIdAndDiaSemana(
            Long estabelecimentoId,
            Integer diaSemana
    );
}
