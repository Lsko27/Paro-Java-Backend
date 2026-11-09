package br.group.paro.estabelecimento.repository;

import br.group.paro.estabelecimento.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstabelecimentoRepository
        extends JpaRepository<Estabelecimento, Long> {

    List<Estabelecimento> findByAtivoTrue();

    List<Estabelecimento> findByProprietarioId(Long usuarioId);

    Optional<Estabelecimento> findByIdAndProprietarioId(
            Long estabelecimentoId,
            Long usuarioId
    );

    List<Estabelecimento> findByCidadeIgnoreCaseAndAtivoTrue(
            String cidade
    );
}