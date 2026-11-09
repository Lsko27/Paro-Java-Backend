package br.group.paro.estabelecimentorecurso.service;

import br.group.paro.common.exception.RecursoNaoEncontradoException;
import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.estabelecimento.repository.EstabelecimentoRepository;
import br.group.paro.estabelecimentorecurso.dto.EstabelecimentoRecursoRequest;
import br.group.paro.estabelecimentorecurso.dto.EstabelecimentoRecursoResponse;
import br.group.paro.estabelecimentorecurso.entity.EstabelecimentoRecurso;
import br.group.paro.estabelecimentorecurso.repository.EstabelecimentoRecursoRepository;
import br.group.paro.recurso.entity.Recurso;
import br.group.paro.recurso.repository.RecursoRepository;
import br.group.paro.usuario.entity.TipoUsuario;
import br.group.paro.usuario.entity.Usuario;
import br.group.paro.usuario.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EstabelecimentoRecursoService {

    private final EstabelecimentoRecursoRepository associacaoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstabelecimentoRecursoService(
            EstabelecimentoRecursoRepository associacaoRepository,
            EstabelecimentoRepository estabelecimentoRepository,
            RecursoRepository recursoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.associacaoRepository = associacaoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.recursoRepository = recursoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public EstabelecimentoRecursoResponse associar(
            Long estabelecimentoId,
            EstabelecimentoRecursoRequest request,
            Long usuarioId
    ) {
        Estabelecimento estabelecimento =
                buscarEstabelecimentoDoProprietario(
                        estabelecimentoId,
                        usuarioId
                );

        Recurso recurso = recursoRepository
                .findById(request.recursoId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Recurso não encontrado"
                        )
                );

        boolean jaExiste = associacaoRepository
                .existsByEstabelecimentoIdAndRecursoId(
                        estabelecimentoId,
                        recurso.getId()
                );

        if (jaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este recurso já está associado ao estabelecimento"
            );
        }

        EstabelecimentoRecurso associacao =
                new EstabelecimentoRecurso(
                        estabelecimento,
                        recurso,
                        request.disponivel(),
                        request.gratuito(),
                        normalizarCondicao(request.condicao())
                );

        EstabelecimentoRecurso salvo =
                associacaoRepository.saveAndFlush(associacao);

        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<EstabelecimentoRecursoResponse> listarDisponiveis(
            Long estabelecimentoId
    ) {
        Estabelecimento estabelecimento =
                estabelecimentoRepository.findById(estabelecimentoId)
                        .filter(Estabelecimento::getAtivo)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Estabelecimento não encontrado"
                                )
                        );

        return associacaoRepository
                .findByEstabelecimentoIdAndDisponivelTrue(
                        estabelecimento.getId()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EstabelecimentoRecursoResponse> listarParaGerenciamento(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        buscarEstabelecimentoDoProprietario(
                estabelecimentoId,
                usuarioId
        );

        return associacaoRepository
                .findByEstabelecimentoId(estabelecimentoId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EstabelecimentoRecursoResponse atualizar(
            Long associacaoId,
            EstabelecimentoRecursoRequest request,
            Long usuarioId
    ) {
        validarPerfilComerciante(usuarioId);

        EstabelecimentoRecurso associacao =
                associacaoRepository
                        .findByIdAndEstabelecimentoProprietarioId(
                                associacaoId,
                                usuarioId
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Associação não encontrada"
                                )
                        );

        if (!associacao.getRecurso().getId()
                .equals(request.recursoId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é permitido alterar o recurso da associação"
            );
        }

        associacao.atualizarDados(
                request.disponivel(),
                request.gratuito(),
                normalizarCondicao(request.condicao())
        );

        return toResponse(associacao);
    }

    @Transactional
    public void remover(Long associacaoId, Long usuarioId) {

        validarPerfilComerciante(usuarioId);

        EstabelecimentoRecurso associacao =
                associacaoRepository
                        .findByIdAndEstabelecimentoProprietarioId(
                                associacaoId,
                                usuarioId
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Associação não encontrada"
                                )
                        );

        associacaoRepository.delete(associacao);
    }

    private Estabelecimento buscarEstabelecimentoDoProprietario(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        validarPerfilComerciante(usuarioId);

        return estabelecimentoRepository
                .findByIdAndProprietarioId(
                        estabelecimentoId,
                        usuarioId
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Estabelecimento não encontrado"
                        )
                );
    }

    private void validarPerfilComerciante(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );

        if (usuario.getTipoUsuario() != TipoUsuario.ESTABELECIMENTO) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas comerciantes podem gerenciar recursos"
            );
        }
    }

    private String normalizarCondicao(String condicao) {

        if (condicao == null || condicao.isBlank()) {
            return null;
        }

        return condicao.trim();
    }

    private EstabelecimentoRecursoResponse toResponse(
            EstabelecimentoRecurso associacao
    ) {
        Recurso recurso = associacao.getRecurso();

        return new EstabelecimentoRecursoResponse(
                associacao.getId(),
                associacao.getEstabelecimento().getId(),
                recurso.getId(),
                recurso.getNome(),
                recurso.getDescricao(),
                associacao.getDisponivel(),
                associacao.getGratuito(),
                associacao.getCondicao()
        );
    }
}