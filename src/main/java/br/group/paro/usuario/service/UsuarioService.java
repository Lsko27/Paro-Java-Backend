package br.group.paro.usuario.service;

import br.group.paro.common.exception.EmailJaCadastradoException;
import br.group.paro.usuario.dto.AtualizarMeuPerfilRequest;
import br.group.paro.usuario.dto.UsuarioCadastroRequest;
import br.group.paro.usuario.dto.UsuarioResponse;
import br.group.paro.usuario.entity.Usuario;
import br.group.paro.usuario.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioCadastroRequest request) {

        String nome = request.nome().trim();

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailJaCadastradoException();
        }

        String senhaHash = passwordEncoder.encode(request.senha());

        Usuario usuario = new Usuario(
                nome,
                email,
                senhaHash,
                request.tipoUsuario()
        );


        Usuario usuarioSalvo = usuarioRepository.saveAndFlush(usuario);

        return toResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado"
                        )
                );

        return toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizarMeuPerfil(
            Long usuarioId,
            AtualizarMeuPerfilRequest request
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuário não encontrado"
                        )
                );

        String nome = request.nome().trim();

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        String telefone = request.telefone();

        if (telefone != null) {
            telefone = telefone.trim();

            if (telefone.isEmpty()) {
                telefone = null;
            }
        }

        boolean emailJaUtilizado =
                usuarioRepository.existsByEmailIgnoreCaseAndIdNot(
                        email,
                        usuarioId
                );

        if (emailJaUtilizado) {
            throw new EmailJaCadastradoException();
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);

        Usuario usuarioAtualizado = usuarioRepository.saveAndFlush(usuario);

        return toResponse(usuarioAtualizado);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTipoUsuario(),
                usuario.getCriadoEm()
        );
    }
}