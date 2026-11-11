
package br.group.paro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfLogoutHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import jakarta.servlet.DispatcherType;

import java.util.Set;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CookieBearerTokenResolver cookieResolver
    ) throws Exception {

        CookieCsrfTokenRepository csrfRepository =
                new CookieCsrfTokenRepository();

        CsrfTokenRequestAttributeHandler csrfHandler =
                new CsrfTokenRequestAttributeHandler();

        Set<String> safeMethods = Set.of(
                "GET",
                "HEAD",
                "OPTIONS",
                "TRACE"
        );

        RequestMatcher csrfMatcher = request -> {
            if (safeMethods.contains(request.getMethod())) {
                return false;
            }

            String path = request.getServletPath();

            return !path.equals("/api/v1/auth/register")
                    && !path.equals("/api/v1/auth/login");
        };

        CsrfFilter csrfFilter = new CsrfFilter(csrfRepository);

        csrfFilter.setRequireCsrfProtectionMatcher(csrfMatcher);
        csrfFilter.setRequestHandler(csrfHandler);

        http
                .cors(Customizer.withDefaults())

                // Substitui a configuração automática de CSRF
                // para não dispensar requisições Bearer via cookie.
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAt(csrfFilter, CsrfFilter.class)

                .logout(logout -> logout
                        .addLogoutHandler(
                                new CsrfLogoutHandler(csrfRepository)
                        )
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(
                                DispatcherType.ERROR
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/health",
                                "/api/v1/auth/register",
                                "/api/v1/auth/login",
                                "/api/v1/auth/csrf"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/estabelecimentos/meus",
                                "/api/v1/estabelecimentos/*/recursos/gerenciar"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/estabelecimentos",
                                "/api/v1/estabelecimentos/{id}",
                                "/api/v1/estabelecimentos/{id}/recursos",
                                "/api/v1/estabelecimentos/{id}/horarios",
                                "/api/v1/estabelecimentos/{id}/avaliacoes",
                                "/api/v1/estabelecimentos/{id}/avaliacoes/resumo"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/recursos"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/estabelecimentos/buscar",
                                "/api/v1/estabelecimentos/pesquisar"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/impacto/resumo"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(cookieResolver)
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
