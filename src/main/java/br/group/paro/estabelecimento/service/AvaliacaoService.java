
package br.group.paro.estabelecimento.service;

import br.group.paro.common.exception.OperacaoNaoPermitidaException;
import br.group.paro.common.exception.RecursoNaoEncontradoException;
import br.group.paro.estabelecimento.dto.AvaliacaoRequest;
import br.group.paro.estabelecimento.dto.AvaliacaoResponse;
import br.group.paro.estabelecimento.dto.ResumoAvaliacoesResponse;
import br.group.paro.estabelecimento.entity.Avaliacao;
import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.estabelecimento.repository.AvaliacaoRepository;
import br.group.paro.estabelecimento.repository.EstabelecimentoRepository;
import br.group.paro.usuario.entity.Usuario;
import br.group.paro.usuario.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public AvaliacaoService(
            AvaliacaoRepository avaliacaoRepository,
            EstabelecimentoRepository estabelecimentoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listar(Long estabelecimentoId) {

        buscarEstabelecimentoAtivo(estabelecimentoId);

        return avaliacaoRepository
                .findByEstabelecimentoIdOrderByCriadoEmDesc(
                        estabelecimentoId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumoAvaliacoesResponse buscarResumo(
            Long estabelecimentoId
    ) {
        buscarEstabelecimentoAtivo(estabelecimentoId);

        Double media = avaliacaoRepository
                .calcularMediaPorEstabelecimento(estabelecimentoId);

        long total = avaliacaoRepository
                .countByEstabelecimentoId(estabelecimentoId);

        return new ResumoAvaliacoesResponse(
                estabelecimentoId,
                media,
                total
        );
    }

    @Transactional
    public AvaliacaoResponse cadastrar(
            Long estabelecimentoId,
            AvaliacaoRequest request,
            Long usuarioId
    ) {
        Estabelecimento estabelecimento =
                buscarEstabelecimentoAtivo(estabelecimentoId);

        Usuario usuario = buscarUsuario(usuarioId);

        validarProprietario(estabelecimento, usuarioId);

        validarAvaliacao(request);

        boolean jaAvaliou = avaliacaoRepository
                .existsByEstabelecimentoIdAndUsuarioId(
                        estabelecimentoId,
                        usuarioId
                );

        if (jaAvaliou) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Você já avaliou este estabelecimento"
            );
        }

        Avaliacao avaliacao = new Avaliacao(
                estabelecimento,
                usuario,
                request.nota(),
                request.comentario()
        );

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        return toResponse(salva);
    }

    @Transactional
    public AvaliacaoResponse atualizarMinha(
            Long estabelecimentoId,
            AvaliacaoRequest request,
            Long usuarioId
    ) {
        buscarEstabelecimentoAtivo(estabelecimentoId);

        validarAvaliacao(request);

        Avaliacao avaliacao = buscarMinhaAvaliacao(
                estabelecimentoId,
                usuarioId
        );

        avaliacao.atualizarDados(
                request.nota(),
                request.comentario()
        );

        return toResponse(avaliacao);
    }

    @Transactional
    public void excluirMinha(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        buscarEstabelecimentoAtivo(estabelecimentoId);

        Avaliacao avaliacao = buscarMinhaAvaliacao(
                estabelecimentoId,
                usuarioId
        );

        avaliacaoRepository.delete(avaliacao);
    }

    private Estabelecimento buscarEstabelecimentoAtivo(
            Long estabelecimentoId
    ) {
        return estabelecimentoRepository
                .findById(estabelecimentoId)
                .filter(Estabelecimento::getAtivo)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estabelecimento não encontrado"
                        )
                );
    }

    private Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );
    }

    private Avaliacao buscarMinhaAvaliacao(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        return avaliacaoRepository
                .findByEstabelecimentoIdAndUsuarioId(
                        estabelecimentoId,
                        usuarioId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Avaliação não encontrada"
                        )
                );
    }

    private void validarProprietario(
            Estabelecimento estabelecimento,
            Long usuarioId
    ) {
        if (estabelecimento.getProprietario()
                .getId().equals(usuarioId)) {

            throw new OperacaoNaoPermitidaException(
                    "O proprietário não pode avaliar o próprio estabelecimento"
            );
        }
    }

    private void validarAvaliacao(AvaliacaoRequest request) {

        if (request == null || request.nota() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A nota é obrigatória"
            );
        }

        if (request.nota() < 1 || request.nota() > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A nota deve estar entre 1 e 5"
            );
        }

        if (request.comentario() != null
                && request.comentario().length() > 1000) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O comentário deve ter no máximo 1000 caracteres"
            );
        }
    }

    private AvaliacaoResponse toResponse(Avaliacao avaliacao) {

        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getEstabelecimento().getId(),
                avaliacao.getUsuario().getId(),
                avaliacao.getUsuario().getNome(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getCriadoEm(),
                avaliacao.getAtualizadoEm()
        );
    }
}
