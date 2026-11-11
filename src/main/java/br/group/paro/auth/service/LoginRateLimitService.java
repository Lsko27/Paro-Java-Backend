
package br.group.paro.auth.service;

import br.group.paro.usuario.entity.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Service
public class LoginRateLimitService {

    private static final int MAX_TENTATIVAS_CONTA = 5;
    private static final int MAX_TENTATIVAS_IP = 30;
    private static final int MAX_REGISTROS = 10_000;
    private static final int TOTAL_BLOQUEIOS = 128;

    private static final Duration JANELA = Duration.ofMinutes(5);

    private final ConcurrentHashMap<String, Tentativas> tentativas =
            new ConcurrentHashMap<>();

    private final Object[] bloqueios = new Object[TOTAL_BLOQUEIOS];
    private final Object controleCapacidade = new Object();

    private final AtomicInteger operacoes = new AtomicInteger();

    public LoginRateLimitService() {
        for (int i = 0; i < TOTAL_BLOQUEIOS; i++) {
            bloqueios[i] = new Object();
        }
    }

    public Usuario autenticarComLimite(
            String ip,
            String email,
            Supplier<Usuario> autenticacao
    ) {
        String ipNormalizado = ip.trim();
        String emailNormalizado = email.trim()
                .toLowerCase(Locale.ROOT);

        String chaveConta = "conta:" + ipNormalizado
                + ":" + emailNormalizado;

        String chaveIp = "ip:" + ipNormalizado;

        Object bloqueio = bloqueios[
                Math.floorMod(
                        ipNormalizado.hashCode(),
                        TOTAL_BLOQUEIOS
                )
                ];

        synchronized (bloqueio) {
            Instant agora = Instant.now();

            verificarLimite(
                    chaveConta,
                    MAX_TENTATIVAS_CONTA,
                    agora
            );

            verificarLimite(
                    chaveIp,
                    MAX_TENTATIVAS_IP,
                    agora
            );

            try {
                Usuario usuario = autenticacao.get();

                // Sucesso limpa apenas as falhas da conta.
                // O limite global por IP permanece.
                tentativas.remove(chaveConta);

                return usuario;

            } catch (ResponseStatusException exception) {
                if (exception.getStatusCode().value() == 401) {
                    registrarFalha(
                            chaveConta,
                            chaveIp,
                            Instant.now()
                    );
                }

                throw exception;
            }
        }
    }

    private void verificarLimite(
            String chave,
            int limite,
            Instant agora
    ) {
        Tentativas registro = tentativas.get(chave);

        if (registro == null) {
            return;
        }

        if (!agora.isBefore(registro.expiraEm())) {
            tentativas.remove(chave, registro);
            return;
        }

        if (registro.quantidade() >= limite) {
            throw limiteExcedido();
        }
    }

    private void registrarFalha(
            String chaveConta,
            String chaveIp,
            Instant agora
    ) {
        synchronized (controleCapacidade) {
            if (operacoes.incrementAndGet() % 100 == 0) {
                limparExpirados(agora);
            }

            int novosRegistros = 0;

            if (!tentativas.containsKey(chaveConta)) {
                novosRegistros++;
            }

            if (!tentativas.containsKey(chaveIp)) {
                novosRegistros++;
            }

            if (tentativas.size() + novosRegistros
                    > MAX_REGISTROS) {
                limparExpirados(agora);

                novosRegistros = 0;

                if (!tentativas.containsKey(chaveConta)) {
                    novosRegistros++;
                }

                if (!tentativas.containsKey(chaveIp)) {
                    novosRegistros++;
                }

                if (tentativas.size() + novosRegistros
                        > MAX_REGISTROS) {
                    throw limiteExcedido();
                }
            }

            incrementar(chaveConta, agora);
            incrementar(chaveIp, agora);
        }
    }

    private void incrementar(String chave, Instant agora) {
        tentativas.compute(chave, (key, atual) -> {
            if (atual == null
                    || !agora.isBefore(atual.expiraEm())) {
                return new Tentativas(
                        1,
                        agora.plus(JANELA)
                );
            }

            return new Tentativas(
                    atual.quantidade() + 1,
                    atual.expiraEm()
            );
        });
    }

    private void limparExpirados(Instant agora) {
        tentativas.entrySet().removeIf(entry ->
                !agora.isBefore(entry.getValue().expiraEm())
        );
    }

    private ResponseStatusException limiteExcedido() {
        return new ResponseStatusException(
                HttpStatus.TOO_MANY_REQUESTS,
                "Muitas tentativas de login. Tente novamente mais tarde."
        );
    }

    private record Tentativas(
            int quantidade,
            Instant expiraEm
    ) {}
}
