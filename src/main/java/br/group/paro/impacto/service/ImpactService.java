
package br.group.paro.impacto.service;

import br.group.paro.impacto.dto.ImpactSummaryResponse;
import br.group.paro.impacto.dto.ResourceImpactResponse;

import jakarta.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImpactService {

    private final EntityManager entityManager;

    public ImpactService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public ImpactSummaryResponse obterResumo() {

        long totalEstabelecimentos = contarEstabelecimentos();
        long totalBairros = contarBairros();
        List<ResourceImpactResponse> recursos = contarRecursos();

        return new ImpactSummaryResponse(
                totalEstabelecimentos,
                totalBairros,
                recursos
        );
    }

    private long contarEstabelecimentos() {
        return entityManager.createQuery("""
                SELECT COUNT(e)
                FROM Estabelecimento e
                WHERE e.ativo = true
                """, Long.class)
                .getSingleResult();
    }

    private long contarBairros() {
        return entityManager.createQuery("""
                SELECT COUNT(DISTINCT CONCAT(
                    e.uf, '|', e.cidade, '|', e.bairro
                ))
                FROM Estabelecimento e
                WHERE e.ativo = true
                  AND e.bairro IS NOT NULL
                """, Long.class)
                .getSingleResult();
    }

    private List<ResourceImpactResponse> contarRecursos() {
        return entityManager.createQuery("""
                SELECT new br.group.paro.impacto.dto.ResourceImpactResponse(
                    r.nome,
                    COUNT(DISTINCT e.id)
                )
                FROM EstabelecimentoRecurso er
                JOIN er.estabelecimento e
                JOIN er.recurso r
                WHERE er.disponivel = true
                  AND e.ativo = true
                GROUP BY r.id, r.nome
                ORDER BY COUNT(DISTINCT e.id) DESC
                """, ResourceImpactResponse.class)
                .getResultList();
    }
}
