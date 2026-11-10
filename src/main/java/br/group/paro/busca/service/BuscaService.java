package br.group.paro.busca.service;

import br.group.paro.busca.dto.BuscaEstabelecimentoResponse;
import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.estabelecimento.repository.EstabelecimentoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class BuscaService {

    private static final double RAIO_TERRA_KM = 6371.0088;

    private final EstabelecimentoRepository estabelecimentoRepository;

    public BuscaService(
            EstabelecimentoRepository estabelecimentoRepository
    ) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<BuscaEstabelecimentoResponse> buscar(
            BigDecimal latitude,
            BigDecimal longitude,
            double raioKm,
            Long recursoId
    ) {
        List<Estabelecimento> estabelecimentos;

        if (recursoId != null) {
            estabelecimentos =
                    estabelecimentoRepository.buscarAtivosPorRecurso(
                            recursoId
                    );
        } else {
            estabelecimentos =
                    estabelecimentoRepository.findByAtivoTrue();
        }

        double latitudeOrigem = latitude.doubleValue();
        double longitudeOrigem = longitude.doubleValue();

        return estabelecimentos.stream()
                .map(estabelecimento -> {

                    double distancia = calcularDistancia(
                            latitudeOrigem,
                            longitudeOrigem,
                            estabelecimento.getLatitude().doubleValue(),
                            estabelecimento.getLongitude().doubleValue()
                    );

                    return toResponse(estabelecimento, distancia);
                })
                .filter(response ->
                        response.distanciaKm() <= raioKm
                )
                .sorted(
                        Comparator.comparingDouble(
                                BuscaEstabelecimentoResponse::distanciaKm
                        )
                )
                .toList();
    }

    private double calcularDistancia(
            double lat1,
            double lon1,
            double lat2,
            double lon2
    ) {
        double diferencaLatitude =
                Math.toRadians(lat2 - lat1);

        double diferencaLongitude =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.pow(Math.sin(diferencaLatitude / 2), 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.pow(Math.sin(diferencaLongitude / 2), 2);

        a = Math.min(1.0, Math.max(0.0, a));

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        return RAIO_TERRA_KM * c;
    }

    private BuscaEstabelecimentoResponse toResponse(
            Estabelecimento estabelecimento,
            double distancia
    ) {
        return new BuscaEstabelecimentoResponse(
                estabelecimento.getId(),
                estabelecimento.getNome(),
                estabelecimento.getDescricao(),
                estabelecimento.getCep(),
                estabelecimento.getLogradouro(),
                estabelecimento.getNumero(),
                estabelecimento.getBairro(),
                estabelecimento.getCidade(),
                estabelecimento.getUf(),
                estabelecimento.getLatitude(),
                estabelecimento.getLongitude(),
                distancia,
                estabelecimento.getImagemUrl()
        );
    }
}