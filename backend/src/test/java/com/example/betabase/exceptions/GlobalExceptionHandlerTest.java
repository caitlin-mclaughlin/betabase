package com.example.betabase.exceptions;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import com.example.betabase.security.JwtService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {
        DummyValidationController.class,
        DummyConstraintViolationController.class,
        DummyExceptionController.class
})
@AutoConfigureMockMvc
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestConfig.class})
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidationErrorReturnsBadRequest() throws Exception {
        String requestBody = "{}"; // Missing required field 'name'

        mockMvc.perform(post("/dummy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("name: must not be blank"));
    }

    @Test
    void testConstraintViolationException() throws Exception {
        mockMvc.perform(get("/constraint"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Constraint Violation"));
    }

    @Test
    void testBadCredentialsException() throws Exception {
        mockMvc.perform(get("/bad-credentials"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Invalid Credentials"));
    }

    @Test
    void testAccessDeniedException() throws Exception {
        mockMvc.perform(get("/access-denied"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Access Denied"));
    }

    @Test
    void testGeneralException() throws Exception {
        mockMvc.perform(get("/general-error"))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
            return http.build();
        }

        @Bean
        @Primary
        public JwtService jwtService() {
            return Mockito.mock(JwtService.class);
        }

        @Bean
        @Primary
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }

        @Bean
        @Primary
        public UserDetailsService userDetailsService() {
            return username -> {
                throw new UsernameNotFoundException("User not found");
            };
        }
    }
}

@RestController
class DummyValidationController {
    @PostMapping("/dummy")
    ResponseEntity<Void> validateDummy(@Valid @RequestBody DummyRequest request) {
        return ResponseEntity.ok().build();
    }

    static class DummyRequest {
        @NotBlank
        public String name;
    }
}

@RestController
class DummyConstraintViolationController {
    @GetMapping("/constraint")
    public void throwConstraintViolation() {
        throw new jakarta.validation.ConstraintViolationException("Simulated ConstraintViolation", java.util.Collections.emptySet());
    }
}

@RestController
class DummyExceptionController {
    @GetMapping("/bad-credentials")
    public void throwBadCredentials() {
        throw new BadCredentialsException("Bad credentials");
    }

    @GetMapping("/access-denied")
    public void throwAccessDenied() {
        throw new AccessDeniedException("Access is denied");
    }

    @GetMapping("/general-error")
    public void throwGeneral() {
        throw new RuntimeException("Something went wrong");
    }
}


