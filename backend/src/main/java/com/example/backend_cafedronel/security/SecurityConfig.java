package com.example.backend_cafedronel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtFilter,
            RestAuthenticationEntryPoint authenticationEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        return http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/favicon.ico", "/api/estado", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "CAJERO", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/productos/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/productos/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/productos/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/inventario/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inventario/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/inventario/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/inventario/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/proveedores/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/proveedores/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/proveedores/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/proveedores/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers(HttpMethod.DELETE, "/api/proveedores/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("ADMIN", "CAJERO", "CONTADOR")
                        .requestMatchers(HttpMethod.POST, "/api/pedidos/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.PATCH, "/api/pedidos/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/ventas/**").hasAnyRole("ADMIN", "CAJERO", "CONTADOR")
                        .requestMatchers(HttpMethod.POST, "/api/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/ventas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/pos/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers("/api/caja/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers("/api/salon/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/categorias-producto")
                        .hasAnyRole("ADMIN", "CAJERO", "INVENTARIO")
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/**").hasAnyRole("ADMIN", "INVENTARIO")
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMIN", "CONTADOR")
                        .requestMatchers("/api/admin/**", "/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "CAJERO", "INVENTARIO", "CONTADOR")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException(username);
        };
    }
}
