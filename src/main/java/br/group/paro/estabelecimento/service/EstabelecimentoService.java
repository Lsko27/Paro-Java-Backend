package br.group.paro.estabelecimento.service;

import br.group.paro.common.exception.OperacaoNaoPermitidaException;
import br.group.paro.common.exception.RecursoNaoEncontradoException;
import br.group.paro.estabelecimento.dto.EstabelecimentoRequest;
import br.group.paro.estabelecimento.dto.EstabelecimentoResponse;
import br.group.paro.estabelecimento.entity.Estabelecimento;
import br.group.paro.estabelecimento.repository.EstabelecimentoRepository;
import br.group.paro.usuario.entity.TipoUsuario;
import br.group.paro.usuario.entity.Usuario;
import br.group.paro.usuario.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public EstabelecimentoService(
            EstabelecimentoRepository estabelecimentoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public EstabelecimentoResponse cadastrar(
            EstabelecimentoRequest request,
            Long usuarioId
    ) {
        Usuario proprietario = buscarUsuario(usuarioId);
        validarPerfilComerciante(proprietario);

        Estabelecimento estabelecimento = new Estabelecimento(
                request.nome().trim(),
                normalizarDescricao(request.descricao()),
                request.cep().trim(),
                request.logradouro().trim(),
                request.numero().trim(),
                request.bairro().trim(),
                request.cidade().trim(),
                request.uf().trim().toUpperCase(Locale.ROOT),
                request.latitude(),
                request.longitude(),
                proprietario
        );

        Estabelecimento salvo =
                estabelecimentoRepository.save(estabelecimento);

        return toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<EstabelecimentoResponse> listarAtivos() {

        return estabelecimentoRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstabelecimentoResponse buscarPorId(Long id) {

        Estabelecimento estabelecimento =
                estabelecimentoRepository.findById(id)
                        .filter(Estabelecimento::getAtivo)
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "Estabelecimento não encontrado"
                                )
                        );

        return toResponse(estabelecimento);
    }

    @Transactional(readOnly = true)
    public List<EstabelecimentoResponse> listarMeus(
            Long usuarioId
    ) {
        Usuario usuario = buscarUsuario(usuarioId);
        validarPerfilComerciante(usuario);

        return estabelecimentoRepository
                .findByProprietarioId(usuarioId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EstabelecimentoResponse atualizar(
            Long estabelecimentoId,
            EstabelecimentoRequest request,
            Long usuarioId
    ) {
        Estabelecimento estabelecimento =
                buscarEstabelecimentoDoProprietario(
                        estabelecimentoId,
                        usuarioId
                );

        estabelecimento.atualizarDados(
                request.nome().trim(),
                normalizarDescricao(request.descricao()),
                request.cep().trim(),
                request.logradouro().trim(),
                request.numero().trim(),
                request.bairro().trim(),
                request.cidade().trim(),
                request.uf().trim().toUpperCase(Locale.ROOT),
                request.latitude(),
                request.longitude()
        );

        return toResponse(estabelecimento);
    }

    @Transactional
    public void desativar(Long estabelecimentoId, Long usuarioId) {

        Estabelecimento estabelecimento =
                buscarEstabelecimentoDoProprietario(
                        estabelecimentoId,
                        usuarioId
                );

        estabelecimento.desativar();
    }

    @Transactional
    public EstabelecimentoResponse ativar(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        Estabelecimento estabelecimento =
                buscarEstabelecimentoDoProprietario(
                        estabelecimentoId,
                        usuarioId
                );

        estabelecimento.ativar();

        return toResponse(estabelecimento);
    }

    private Usuario buscarUsuario(Long usuarioId) {

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );
    }

    private void validarPerfilComerciante(Usuario usuario) {

        if (usuario.getTipoUsuario() != TipoUsuario.ESTABELECIMENTO) {
            throw new OperacaoNaoPermitidaException(
                    "Apenas comerciantes podem gerenciar estabelecimentos"
            );
        }
    }

    private Estabelecimento buscarEstabelecimentoDoProprietario(
            Long estabelecimentoId,
            Long usuarioId
    ) {
        Usuario usuario = buscarUsuario(usuarioId);
        validarPerfilComerciante(usuario);

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

    private String normalizarDescricao(String descricao) {

        if (descricao == null || descricao.isBlank()) {
            return null;
        }

        return descricao.trim();
    }

    private EstabelecimentoResponse toResponse(
            Estabelecimento estabelecimento
    ) {
        return new EstabelecimentoResponse(
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
                estabelecimento.getAtivo(),
                estabelecimento.getCriadoEm(),
                estabelecimento.getProprietario().getId()
        );
    }
}