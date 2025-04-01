package gr.atc.urbreath.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class SecurityConfigTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @DisplayName("POST Request - Failure - Requires authentication")
    void postNbsEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(post("/api/nbs/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Request to non-NBS endpoint should require authentication")
    void nonNbsEndpointsShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/random/endpoint"))
                .andExpect(status().isUnauthorized());
    }
}