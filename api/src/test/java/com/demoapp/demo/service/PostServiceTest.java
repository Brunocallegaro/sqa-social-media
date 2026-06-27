package com.demoapp.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.demoapp.demo.model.UserPostReaction;
import com.demoapp.demo.repository.UserPostReactionRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - PostService")
class PostServiceTest {

    @Mock
    private UserPostReactionRepository reactionRepository;

    @InjectMocks
    private PostService postService;

    // ──────────────────────────────────────────
    // toggleLike
    // ──────────────────────────────────────────
    @Test
    @DisplayName("toggleLike: deve criar like quando não existe reação anterior")
    void toggleLike_semReacaoExistente_deveCriarLike() {
        when(reactionRepository.findByUserIdAndPostId(1L, 10L)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(UserPostReaction.class))).thenReturn(new UserPostReaction());

        Map<String, Object> result = postService.toggleLike(10L, 1L);

        assertTrue((Boolean) result.get("liked"));
        assertEquals(10L, result.get("postId"));
        verify(reactionRepository, times(1)).save(any(UserPostReaction.class));
    }

    @Test
    @DisplayName("toggleLike: deve remover like quando já existe reação")
    void toggleLike_comReacaoExistente_deveRemoverLike() {
        UserPostReaction existing = new UserPostReaction();
        existing.setUserId(1L);
        existing.setPostId(10L);

        when(reactionRepository.findByUserIdAndPostId(1L, 10L)).thenReturn(Optional.of(existing));

        Map<String, Object> result = postService.toggleLike(10L, 1L);

        assertFalse((Boolean) result.get("liked"));
        assertEquals(10L, result.get("postId"));
        verify(reactionRepository, times(1)).delete(existing);
    }

    @Test
    @DisplayName("toggleLike: resultado deve conter os campos postId e liked")
    void toggleLike_resultado_deveConterCamposCorretos() {
        when(reactionRepository.findByUserIdAndPostId(2L, 5L)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(UserPostReaction.class))).thenReturn(new UserPostReaction());

        Map<String, Object> result = postService.toggleLike(5L, 2L);

        assertTrue(result.containsKey("postId"));
        assertTrue(result.containsKey("liked"));
    }

    // ──────────────────────────────────────────
    // getLikedPosts - limites de paginação
    // ──────────────────────────────────────────
    @Test
    @DisplayName("getLikedPosts: deve retornar lista vazia quando usuário não tem likes")
    void getLikedPosts_semLikes_deveRetornarListaVazia() {
        when(reactionRepository.findByUserId(99L)).thenReturn(List.of());

        Map<String, Object> result = postService.getLikedPosts(99L, 10, 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> posts = (List<Map<String, Object>>) result.get("posts");
        assertEquals(0, posts.size());
        assertEquals(0, result.get("total"));
    }
}
