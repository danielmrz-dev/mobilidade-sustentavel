package br.com.grupo.mobilidade.config;

import br.com.grupo.mobilidade.security.JwtAuthenticationFilter;
import br.com.grupo.mobilidade.security.RestAccessDeniedHandler;
import br.com.grupo.mobilidade.security.RestAuthenticationEntryPoint;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] ROTAS_PUBLICAS = {
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                // API stateless com JWT: sem sessao e sem CSRF
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publico
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/registro").permitAll()
                        .requestMatchers(ROTAS_PUBLICAS).permitAll()

                        // Qualquer usuario logado pode resgatar recompensa
                        .requestMatchers(HttpMethod.POST, "/recompensas/*/resgates").authenticated()

                        // Cadastros administrativos: somente ADMIN
                        .requestMatchers(HttpMethod.POST, "/modais", "/empresas", "/recompensas").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/modais/**", "/empresas/**", "/recompensas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/modais/**", "/empresas/**", "/recompensas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasRole("ADMIN")

                        // Todo o resto exige login
                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Impede que o Spring Boot registre o filtro JWT uma segunda vez fora da cadeia do Spring Security.
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> desabilitarRegistroAutomaticoJwt(JwtAuthenticationFilter filtro) {
        FilterRegistrationBean<JwtAuthenticationFilter> registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Location"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
