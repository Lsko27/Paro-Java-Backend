
package br.group.paro.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class PasswordResetEmailService {

    private final RestClient restClient;
    private final String remetente;

    public PasswordResetEmailService(
            @Value("${app.mail.resend-api-key}") String apiKey,
            @Value("${app.mail.from}") String remetente
    ) {
        this.remetente = remetente;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .build();
    }

    public void enviarLink(String email, String link) {

        String html = """
                <h2>Redefinição de senha — Parô</h2>
                <p>Recebemos uma solicitação para
                redefinir a senha da sua conta.</p>
                <p>
                  <a href="%s">Redefinir minha senha</a>
                </p>
                <p>Este link expira em 15 minutos
                e só pode ser utilizado uma vez.</p>
                <p>Se você não fez esta solicitação,
                ignore esta mensagem.</p>
                """.formatted(link);

        restClient.post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "from", remetente,
                        "to", List.of(email),
                        "subject", "Redefina sua senha — Parô",
                        "html", html
                ))
                .retrieve()
                .toBodilessEntity();
    }
}
