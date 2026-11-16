
package br.group.paro.auth.repository;

import br.group.paro.auth.entity.PasswordResetToken;
import br.group.paro.usuario.entity.Usuario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken>
    findFirstByUsuarioOrderByCriadoEmDesc(Usuario usuario);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM PasswordResetToken r
        JOIN FETCH r.usuario
        WHERE r.tokenHash = :hash
        """)
    Optional<PasswordResetToken> findByHashForUpdate(
            @Param("hash") String hash
    );

    @Modifying
    @Query("""
        UPDATE PasswordResetToken r
        SET r.utilizadoEm = :agora
        WHERE r.usuario = :usuario
          AND r.utilizadoEm IS NULL
        """)
    int invalidarTokens(
            @Param("usuario") Usuario usuario,
            @Param("agora") LocalDateTime agora
    );
}
