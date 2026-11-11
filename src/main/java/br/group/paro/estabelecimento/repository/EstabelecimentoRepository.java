package br.group.paro.estabelecimento.repository;

import br.group.paro.estabelecimento.entity.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    @Query("""
    SELECT e
    FROM Estabelecimento e
    WHERE e.ativo = true
      AND EXISTS (
          SELECT er.id
          FROM EstabelecimentoRecurso er
          WHERE er.estabelecimento = e
            AND er.recurso.id = :recursoId
            AND er.disponivel = true
      )
    """)
    List<Estabelecimento> buscarAtivosPorRecurso(
            @Param("recursoId") Long recursoId
    );


    @Query("""
    SELECT e
    FROM Estabelecimento e
    WHERE e.ativo = true
      AND (
          LOWER(e.nome) LIKE CONCAT('%', :termo, '%') ESCAPE '\\'
          OR LOWER(e.logradouro) LIKE CONCAT('%', :termo, '%') ESCAPE '\\'
          OR LOWER(e.bairro) LIKE CONCAT('%', :termo, '%') ESCAPE '\\'
          OR LOWER(e.cidade) LIKE CONCAT('%', :termo, '%') ESCAPE '\\'
      )
    ORDER BY e.nome ASC
    """)
    List<Estabelecimento> buscarAtivosPorTermo(
            @Param("termo") String termo
    );


}