
package br.group.paro.favorito.repository;

import br.group.paro.favorito.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository
        extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuarioIdOrderByIdDesc(
            Long usuarioId
    );

    Optional<Favorito> findByUsuarioIdAndEstabelecimentoId(
            Long usuarioId,
            Long estabelecimentoId
    );

    boolean existsByUsuarioIdAndEstabelecimentoId(
            Long usuarioId,
            Long estabelecimentoId
    );

    @Query("""
            SELECT f
            FROM Favorito f
            WHERE f.usuarioId = :usuarioId
              AND EXISTS (
                  SELECT e.id
                  FROM Estabelecimento e
                  WHERE e.id = f.estabelecimentoId
                    AND e.ativo = true
              )
            ORDER BY f.id DESC
            """)
    List<Favorito> findFavoritosAtivos(
            @Param("usuarioId") Long usuarioId
    );
}
