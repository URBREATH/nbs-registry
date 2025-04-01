package gr.atc.urbreath.config;

import gr.atc.urbreath.filter.RateLimitingFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestSecurityConfig {
    
    @Bean
    @Primary
    public RateLimitingFilter testRateLimitingFilter() {
        // Mock Rate Limiting Filter
        return new RateLimitingFilter() {
            @Override
            protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
                return true;
            }
        };
    }
    
    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        // Mock the JWT decoder
        JwtDecoder decoder = mock(JwtDecoder.class);
        when(decoder.decode(anyString())).thenThrow(new RuntimeException("Test JWT decoding not implemented"));
        return decoder;
    }
}