package com.motofix.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // El GET de las motos es público
                .requestMatchers(HttpMethod.GET, "/api/v1/motos/**").permitAll()

                // Los ADMIN (Jefe) pueden crear y borrar motos
                .requestMatchers(HttpMethod.POST, "/api/v1/motos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/motos/**").hasRole("ADMIN")

                // ADMIN y USER (Mecánico) pueden actualizar motos
                .requestMatchers(HttpMethod.PUT, "/api/v1/motos/**").hasAnyRole("ADMIN", "USER")

                // Ambos pueden ver la lista de clientes o buscar por ID
                .requestMatchers(HttpMethod.GET, "/api/v1/clients/**").hasAnyRole("ADMIN", "USER")

                // Solo el ADMIN (Jefe) puede crear, modificar o borrar clientes
                .requestMatchers(HttpMethod.POST, "/api/v1/clients").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/clients/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/clients/**").hasRole("ADMIN")

                // Cualquier otra solicitud no mapeada requiere estar autenticado
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // 1. Definimos el codificador de contraseñas oficial (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Creamos el usuario usando el encriptador
    @Bean
    public UserDetailsService userDetailsService() {
        // 1. Usuario Mecánico (USER)
        UserDetails user = User.builder()
                .username("mecanico")
                .password(passwordEncoder().encode("mecanico123"))
                .roles("USER")
                .build();
        
        // 2. Usuario Jefe de Taller (ADMIN)
        UserDetails admin = User.builder()
                .username("jefetaller")
                .password(passwordEncoder().encode("jefetaller123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}