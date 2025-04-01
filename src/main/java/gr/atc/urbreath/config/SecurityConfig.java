package gr.atc.urbreath.config;

import gr.atc.urbreath.security.UnauthorizedEntryPoint;
import gr.atc.urbreath.filter.RateLimitingFilter;
import gr.atc.urbreath.security.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

        @Value("${spring.security.cors.domains}")
        private String corsDomainsRaw;

        /**
         * Public Filter Chain - No Config for Resource Server
         * @param http : HttpSecurity
         * @return SecurityFilterChain
         */
        @Bean
        public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
                logger.debug("Configuring public security filter chain...");
                http
                        .securityMatcher("/api/nbs/**")
                        .authorizeHttpRequests(authz -> authz
                                .requestMatchers(HttpMethod.GET).permitAll() // Allow GET without auth
                                .anyRequest().denyAll() // Block non-GET methods
                        )
                        .addFilterBefore(new RateLimitingFilter(), SecurityContextHolderFilter.class)
                        .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                        .csrf(AbstractHttpConfigurer::disable)
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                logger.debug("Public security filter chain configured.");
                return http.build();
        }

        /**
         * Initialize and Configure Security Filter Chain of HTTP connection
         * 
         * @param http       : HttpSecurity
         * @param entryPoint : UnauthorizedEntryPoint -> To add proper API Response to the
         *                   authorized request
         * @return SecurityFilterChain
         */
        @Bean
        public SecurityFilterChain securedSecurityFilterChain(HttpSecurity http, UnauthorizedEntryPoint entryPoint)
                        throws Exception {
                logger.debug("Configuring secured security filter chain...");
                // Convert Keycloak Roles with class to Spring Security Roles
                JwtAuthConverter jwtAuthConverter = new JwtAuthConverter();

                // Set Session to Stateless so not to keep any information about the JWT
                http
                        .securityMatcher("/**") // Applies to all other endpoint except the permitted
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                        .csrf(AbstractHttpConfigurer::disable)
                        .addFilterBefore(new RateLimitingFilter(), SecurityContextHolderFilter.class)
                        .exceptionHandling(exc -> exc.authenticationEntryPoint(entryPoint))
                        .authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
                        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtCustomizer ->
                                jwtCustomizer.jwtAuthenticationConverter(jwtAuthConverter)
                        ));
                logger.debug("Secured security filter chain configured.");
                return http.build();
        }

        /**
         * Settings for CORS
         *
         * @return CorsConfigurationSource
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                // Split the string into a list of domains
                List<String> corsDomains = List.of(corsDomainsRaw.split(","));

                // Set CORS configuration
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(corsDomains);
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(86400L);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

}
