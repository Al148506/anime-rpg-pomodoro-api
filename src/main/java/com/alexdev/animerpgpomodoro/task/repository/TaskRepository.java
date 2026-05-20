package com.alexdev.animerpgpomodoro.task.repository;

import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByUserEmailAndCompletedTrue(String email);

    List<Task> findByUserEmailAndCompletedFalse(String email);

    List<Task> findByUserEmailAndPriority(
            String email,
            TaskPriority priority
    );

    List<Task> findByUserEmailAndCategoryId(
            String email,
            String categoryId
    );;

    List<Task> findByUserEmail(String email);

    Optional<Task> findByIdAndUserEmail(String id, String email);
}