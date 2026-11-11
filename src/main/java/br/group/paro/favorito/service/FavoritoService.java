
package br.group.paro.favorito.service;

import br.group.paro.common.exception.RecursoNaoEncontradoException;
import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.estabelecimento.repository.EstabelecimentoRepository;
import br.group.paro.favorito.entity.Favorito;
import br.group.paro.favorito.repository.FavoritoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public FavoritoService(
            FavoritoRepository favoritoRepository,
            EstabelecimentoRepository estabelecimentoRepository
    ) {
        this.favoritoRepository = favoritoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Favorito> listar(Long usuarioId) {
        return favoritoRepository.findFavoritosAtivos(usuarioId);
    }

    @Transactional
    public Favorito adicionar(
            Long usuarioId,
            Long estabelecimentoId
    ) {
        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(estabelecimentoId)
                .filter(Estabelecimento::getAtivo)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estabelecimento não encontrado"
                        )
                );

        return favoritoRepository
                .findByUsuarioIdAndEstabelecimentoId(
                        usuarioId,
                        estabelecimento.getId()
                )
                .orElseGet(() ->
                        favoritoRepository.save(
                                new Favorito(
                                        usuarioId,
                                        estabelecimento.getId()
                                )
                        )
                );
    }

    @Transactional
    public void remover(
            Long usuarioId,
            Long estabelecimentoId
    ) {
        favoritoRepository
                .findByUsuarioIdAndEstabelecimentoId(
                        usuarioId,
                        estabelecimentoId
                )
                .ifPresent(favoritoRepository::delete);
    }
}
