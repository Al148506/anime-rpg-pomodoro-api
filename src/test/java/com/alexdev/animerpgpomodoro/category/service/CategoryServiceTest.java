package com.alexdev.animerpgpomodoro.category.service;

import com.alexdev.animerpgpomodoro.category.dto.CategoryRequest;
import com.alexdev.animerpgpomodoro.category.dto.CategoryResponse;
import com.alexdev.animerpgpomodoro.category.entity.Category;
import com.alexdev.animerpgpomodoro.category.repository.CategoryRepository;
import com.alexdev.animerpgpomodoro.common.exception.DuplicateResourceException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private static final String CATEGORY_ID = "category-uuid";
    private static final String CATEGORY_NAME = "Work";
    private static final String CATEGORY_SLUG = "work";
    private static final String CATEGORY_COLOR = "#FF0000";
    private static final String CATEGORY_EMOJI = "💼";

    private Category createCategory() {
        return Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .slug(CATEGORY_SLUG)
                .color(CATEGORY_COLOR)
                .emoji(CATEGORY_EMOJI)
                .build();
    }

    @Nested
    class GetAllCategories {

        @Test
        void shouldReturnListOfCategoriesWhenCategoriesExist() {
            var category = createCategory();

            when(categoryRepository.findAll()).thenReturn(List.of(category));

            List<CategoryResponse> result = categoryService.getAllCategories();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(CATEGORY_ID);
            assertThat(result.get(0).name()).isEqualTo(CATEGORY_NAME);
            assertThat(result.get(0).slug()).isEqualTo(CATEGORY_SLUG);
            assertThat(result.get(0).color()).isEqualTo(CATEGORY_COLOR);
            assertThat(result.get(0).emoji()).isEqualTo(CATEGORY_EMOJI);
            verify(categoryRepository).findAll();
        }

        @Test
        void shouldReturnEmptyListWhenNoCategoriesExist() {
            when(categoryRepository.findAll()).thenReturn(List.of());

            List<CategoryResponse> result = categoryService.getAllCategories();

            assertThat(result).isEmpty();
            verify(categoryRepository).findAll();
        }
    }

    @Nested
    class CreateCategory {

        @Test
        void shouldSaveAndReturnCategoryResponseWhenSlugIsUnique() {
            var request = new CategoryRequest(CATEGORY_NAME, CATEGORY_SLUG, CATEGORY_COLOR, CATEGORY_EMOJI);
            var category = createCategory();

            when(categoryRepository.existsBySlug(CATEGORY_SLUG)).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            CategoryResponse result = categoryService.createCategory(request);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(CATEGORY_ID);
            assertThat(result.name()).isEqualTo(CATEGORY_NAME);
            assertThat(result.slug()).isEqualTo(CATEGORY_SLUG);
            assertThat(result.color()).isEqualTo(CATEGORY_COLOR);
            assertThat(result.emoji()).isEqualTo(CATEGORY_EMOJI);
            verify(categoryRepository).existsBySlug(CATEGORY_SLUG);
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        void shouldThrowDuplicateResourceExceptionWhenSlugAlreadyExists() {
            var request = new CategoryRequest(CATEGORY_NAME, CATEGORY_SLUG, CATEGORY_COLOR, CATEGORY_EMOJI);

            when(categoryRepository.existsBySlug(CATEGORY_SLUG)).thenReturn(true);

            assertThatThrownBy(() -> categoryService.createCategory(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Category slug already exists");

            verify(categoryRepository).existsBySlug(CATEGORY_SLUG);
            verify(categoryRepository, never()).save(any());
        }
    }
}
