package com.alexdev.animerpgpomodoro.task.repository;

import com.alexdev.animerpgpomodoro.task.entity.Task;
import com.alexdev.animerpgpomodoro.task.entity.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

    List<Task> findByCompletedTrue();

    List<Task> findByCompletedFalse();

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByCategoryId(String categoryId);
}