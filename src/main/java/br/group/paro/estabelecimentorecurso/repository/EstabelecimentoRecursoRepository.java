package br.group.paro.estabelecimentorecurso.repository;

import br.group.paro.estabelecimentorecurso.entity.EstabelecimentoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstabelecimentoRecursoRepository
        extends JpaRepository<EstabelecimentoRecurso, Long> {

    List<EstabelecimentoRecurso> findByEstabelecimentoId(
            Long estabelecimentoId
    );

    List<EstabelecimentoRecurso>
    findByEstabelecimentoIdAndDisponivelTrue(
            Long estabelecimentoId
    );

    Optional<EstabelecimentoRecurso>
    findByEstabelecimentoIdAndRecursoId(
            Long estabelecimentoId,
            Long recursoId
    );

    boolean existsByEstabelecimentoIdAndRecursoId(
            Long estabelecimentoId,
            Long recursoId
    );

    Optional<EstabelecimentoRecurso>
    findByIdAndEstabelecimentoProprietarioId(
            Long id,
            Long usuarioId
    );
}