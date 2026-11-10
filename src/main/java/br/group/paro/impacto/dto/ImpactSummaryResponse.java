package br.group.paro.impacto.dto;

import java.util.List;

public record ImpactSummaryResponse(
        long totalEstabelecimentos,
        long totalBairros,
        List<ResourceImpactResponse> recursos
) {
}