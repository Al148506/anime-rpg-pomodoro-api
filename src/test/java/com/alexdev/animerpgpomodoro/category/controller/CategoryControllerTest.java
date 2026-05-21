package com.alexdev.animerpgpomodoro.category.controller;

import com.alexdev.animerpgpomodoro.auth.security.JwtAuthenticationFilter;
import com.alexdev.animerpgpomodoro.category.dto.CategoryRequest;
import com.alexdev.animerpgpomodoro.category.dto.CategoryResponse;
import com.alexdev.animerpgpomodoro.category.service.CategoryService;
import com.alexdev.animerpgpomodoro.common.AbstractControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("Should get all categories successfully")
    void shouldGetAllCategoriesSuccessfully() throws Exception {
        var response = new CategoryResponse("uuid-1", "Work", "work", "#FF0000", "💼");

        when(categoryService.getAllCategories()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].name").value("Work"))
                .andExpect(jsonPath("$[0].slug").value("work"))
                .andExpect(jsonPath("$[0].color").value("#FF0000"))
                .andExpect(jsonPath("$[0].emoji").value("💼"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("Should create category successfully")
    void shouldCreateCategorySuccessfully() throws Exception {
        var request = new CategoryRequest("Study", "study", "#00FF00", "📚");
        var response = new CategoryResponse("uuid-2", "Study", "study", "#00FF00", "📚");

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uuid-2"))
                .andExpect(jsonPath("$.name").value("Study"))
                .andExpect(jsonPath("$.slug").value("study"))
                .andExpect(jsonPath("$.color").value("#00FF00"))
                .andExpect(jsonPath("$.emoji").value("📚"));

        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }
}
