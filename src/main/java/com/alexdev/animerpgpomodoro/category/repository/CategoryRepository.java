package com.alexdev.animerpgpomodoro.category.repository;

import com.alexdev.animerpgpomodoro.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsBySlug(String slug);

}