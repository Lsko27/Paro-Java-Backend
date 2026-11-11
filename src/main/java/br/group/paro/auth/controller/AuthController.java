
package br.group.paro.auth.controller;

import br.group.paro.auth.dto.LoginRequest;
import br.group.paro.auth.dto.LoginResponse;
import br.group.paro.auth.service.AuthService;
import br.group.paro.auth.service.LoginRateLimitService;
import br.group.paro.usuario.dto.UsuarioResponse;
import br.group.paro.usuario.entity.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimitService loginRateLimitService;
    private final boolean cookieSecure;
    private final long expirationMinutes;

    public AuthController(
            AuthService authService,
            LoginRateLimitService loginRateLimitService,
            @Value("${app.auth.cookie-secure}") boolean cookieSecure,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.authService = authService;
        this.loginRateLimitService = loginRateLimitService;
        this.cookieSecure = cookieSecure;
        this.expirationMinutes = expirationMinutes;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        String email = request.email();

        // Verifica se o usuário atingiu o limite de tentativas.
        Usuario usuario = loginRateLimitService.autenticarComLimite(
                ip,
                email,
                () -> authService.autenticar(request)
        );

        String token = authService.gerarToken(usuario);

        ResponseCookie cookie = ResponseCookie
                .from("paro_token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(expirationMinutes))
                .build();

        LoginResponse response = new LoginResponse(
                "Login realizado com sucesso",
                authService.toResponse(usuario)
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long usuarioId = Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                authService.buscarUsuarioAutenticado(usuarioId)
        );
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of(
                "headerName", token.getHeaderName(),
                "token", token.getToken()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        ResponseCookie cookie = ResponseCookie
                .from("paro_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
