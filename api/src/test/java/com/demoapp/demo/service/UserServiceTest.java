package com.demoapp.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.demoapp.demo.model.User;
import com.demoapp.demo.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ──────────────────────────────────────────
    // isEmailValid
    // ──────────────────────────────────────────
    @Test
    @DisplayName("isEmailValid: deve retornar true para email com @")
    void isEmailValid_comArroba_deveRetornarTrue() {
        assertTrue(userService.isEmailValid("usuario@example.com"));
    }

    @Test
    @DisplayName("isEmailValid: deve retornar false para email sem @")
    void isEmailValid_semArroba_deveRetornarFalse() {
        assertFalse(userService.isEmailValid("usuarioexemplo.com"));
    }

    @Test
    @DisplayName("isEmailValid: deve retornar false para email nulo")
    void isEmailValid_nulo_deveRetornarFalse() {
        assertFalse(userService.isEmailValid(null));
    }

    // ──────────────────────────────────────────
    // isPasswordValid
    // ──────────────────────────────────────────
    @Test
    @DisplayName("isPasswordValid: deve retornar true para senha válida")
    void isPasswordValid_senhaValida_deveRetornarTrue() {
        assertTrue(userService.isPasswordValid("Senha@123"));
    }

    @Test
    @DisplayName("isPasswordValid: deve retornar false para senha sem maiúscula")
    void isPasswordValid_semMaiuscula_deveRetornarFalse() {
        assertFalse(userService.isPasswordValid("senha@123"));
    }

    @Test
    @DisplayName("isPasswordValid: deve retornar false para senha sem caractere especial")
    void isPasswordValid_semEspecial_deveRetornarFalse() {
        assertFalse(userService.isPasswordValid("Senha1234"));
    }

    @Test
    @DisplayName("isPasswordValid: deve retornar false para senha curta")
    void isPasswordValid_muitoCurta_deveRetornarFalse() {
        assertFalse(userService.isPasswordValid("S@1a"));
    }

    @Test
    @DisplayName("isPasswordValid: deve retornar false para senha vazia")
    void isPasswordValid_vazia_deveRetornarFalse() {
        assertFalse(userService.isPasswordValid(""));
    }

    // ──────────────────────────────────────────
    // createUser
    // ──────────────────────────────────────────
    @Test
    @DisplayName("createUser: deve salvar e retornar o usuário criado")
    void createUser_deveSalvarUsuario() {
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("novo@email.com");
        savedUser.setPassword("Senha@123");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("novo@email.com", "Senha@123");

        assertNotNull(result);
        assertEquals("novo@email.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ──────────────────────────────────────────
    // findByEmail
    // ──────────────────────────────────────────
    @Test
    @DisplayName("findByEmail: deve retornar o usuário quando encontrado")
    void findByEmail_usuarioExistente_deveRetornarUsuario() {
        User user = new User();
        user.setEmail("existe@email.com");
        when(userRepository.findByEmail("existe@email.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("existe@email.com");

        assertNotNull(result);
        assertEquals("existe@email.com", result.getEmail());
    }

    @Test
    @DisplayName("findByEmail: deve retornar null quando não encontrado")
    void findByEmail_usuarioInexistente_deveRetornarNull() {
        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        User result = userService.findByEmail("naoexiste@email.com");

        assertNull(result);
    }
}
