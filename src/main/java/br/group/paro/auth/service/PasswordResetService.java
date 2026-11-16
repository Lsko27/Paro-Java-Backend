
package br.group.paro.auth.service;

import br.group.paro.auth.entity.PasswordResetToken;
import br.group.paro.auth.repository.PasswordResetTokenRepository;
import br.group.paro.usuario.entity.Usuario;
import br.group.paro.usuario.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

@Service
public class PasswordResetService {

    private static final Logger log =
            LoggerFactory.getLogger(PasswordResetService.class);

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordResetEmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final String frontendUrl;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordResetEmailService emailService,
            PasswordEncoder passwordEncoder,
            PlatformTransactionManager transactionManager,
            @Value("${app.frontend.url}") String frontendUrl
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate =
                new TransactionTemplate(transactionManager);
        this.frontendUrl = frontendUrl;
    }

    public void solicitarRecuperacao(String email) {
        String emailNormalizado = email.trim()
                .toLowerCase(Locale.ROOT);

        Optional<EmailPendente> pendente =
                transactionTemplate.execute(status -> {

                    Optional<Usuario> encontrado =
                            usuarioRepository.findForPasswordReset(
                                    emailNormalizado
                            );

                    if (encontrado.isEmpty()) {
                        return Optional.<EmailPendente>empty();
                    }

                    Usuario usuario = encontrado.get();
                    LocalDateTime agora =
                            LocalDateTime.now(ZoneOffset.UTC);

                    boolean solicitadoRecentemente = tokenRepository
                            .findFirstByUsuarioOrderByCriadoEmDesc(usuario)
                            .map(ultimo ->
                                    ultimo.getCriadoEm().isAfter(
                                            agora.minusMinutes(1)
                                    )
                            )
                            .orElse(false);

                    if (solicitadoRecentemente) {
                        return Optional.<EmailPendente>empty();
                    }

                    String token = gerarToken();
                    String hash = calcularHash(token);

                    // Novo pedido invalida os links anteriores.
                    tokenRepository.invalidarTokens(usuario, agora);

                    PasswordResetToken registro =
                            new PasswordResetToken(
                                    usuario,
                                    hash,
                                    agora,
                                    agora.plusMinutes(15)
                            );

                    tokenRepository.save(registro);

                    String url = frontendUrl.replaceAll("/+$", "")
                            + "/reset-password?token=" + token;

                    return Optional.of(
                            new EmailPendente(usuario.getEmail(), url)
                    );
                });

        if (pendente == null || pendente.isEmpty()) {
            return;
        }

        // O envio acontece depois da transação de banco.
        EmailPendente envio = pendente.get();

        try {
            emailService.enviarLink(
                    envio.destinatario(),
                    envio.link()
            );
        } catch (Exception exception) {
            // Não revelar se o e-mail está cadastrado.
            // Não registrar o token nem o link nos logs.
            log.error(
                    "Falha ao enviar e-mail de recuperação.",
                    exception
            );
        }
    }

    @Transactional
    public void redefinirSenha(
            String token,
            String novaSenha
    ) {
        if (token == null
                || !token.matches("[A-Za-z0-9_-]{43}")) {
            throw tokenInvalido();
        }

        String hash = calcularHash(token);

        PasswordResetToken registro = tokenRepository
                .findByHashForUpdate(hash)
                .orElseThrow(this::tokenInvalido);

        LocalDateTime agora =
                LocalDateTime.now(ZoneOffset.UTC);

        if (registro.getUtilizadoEm() != null
                || !agora.isBefore(registro.getExpiraEm())) {
            throw tokenInvalido();
        }

        Usuario usuario = registro.getUsuario();

        usuario.setSenhaHash(
                passwordEncoder.encode(novaSenha)
        );

        // Invalida inclusive outros links ainda ativos.
        tokenRepository.invalidarTokens(usuario, agora);
    }

    private String gerarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String calcularHash(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 indisponível",
                    exception
            );
        }
    }

    private ResponseStatusException tokenInvalido() {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Token inválido, expirado ou já utilizado"
        );
    }

    private record EmailPendente(
            String destinatario,
            String link
    ) {}
}
