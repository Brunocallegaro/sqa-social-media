package com.demoapp.demo.controller;

import com.demoapp.demo.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Testes de integração - AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ──────────────────────────────────────────
    // POST /auth/signup
    // ──────────────────────────────────────────
    @Test
    @DisplayName("signup: deve criar usuário com dados válidos e retornar 200")
    void signup_dadosValidos_deveRetornar200() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("novo@email.com");
        dto.setPassword("Senha@123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    @DisplayName("signup: deve retornar 422 para email inválido")
    void signup_emailInvalido_deveRetornar422() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("emailsemarroba");
        dto.setPassword("Senha@123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("E-mail inválido"));
    }

    @Test
    @DisplayName("signup: deve retornar 422 para senha inválida")
    void signup_senhaInvalida_deveRetornar422() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("valido@email.com");
        dto.setPassword("fraca");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Senha inválida"));
    }

    @Test
    @DisplayName("signup: deve retornar 409 para email já cadastrado")
    void signup_emailDuplicado_deveRetornar409() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("duplicado@email.com");
        dto.setPassword("Senha@123");

        // Primeiro cadastro
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        // Segundo cadastro com mesmo email
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("E-mail já está em uso"));
    }

    // ──────────────────────────────────────────
    // POST /auth/signin
    // ──────────────────────────────────────────
    @Test
    @DisplayName("signin: deve autenticar com credenciais válidas e retornar 200")
    void signin_credenciaisValidas_deveRetornar200() throws Exception {
        // Cadastrar primeiro
        UserDTO dto = new UserDTO();
        dto.setEmail("login@email.com");
        dto.setPassword("Senha@123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // Fazer login
        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("login@email.com"));
    }

    @Test
    @DisplayName("signin: deve retornar 401 para senha incorreta")
    void signin_senhaErrada_deveRetornar401() throws Exception {
        UserDTO cadastro = new UserDTO();
        cadastro.setEmail("outro@email.com");
        cadastro.setPassword("Senha@123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cadastro)));

        UserDTO login = new UserDTO();
        login.setEmail("outro@email.com");
        login.setPassword("SenhaErrada@99");

        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));
    }

    // ──────────────────────────────────────────
    // POST /auth/reset-password
    // ──────────────────────────────────────────
    @Test
    @DisplayName("reset-password: deve retornar 200 para email existente")
    void resetPassword_emailExistente_deveRetornar200() throws Exception {
        // Cadastrar usuário
        UserDTO dto = new UserDTO();
        dto.setEmail("reset@email.com");
        dto.setPassword("Senha@123");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        // Reset
        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"reset@email.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("reset-password: deve retornar 404 para email não cadastrado")
    void resetPassword_emailInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"naocadastrado@email.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }
}
