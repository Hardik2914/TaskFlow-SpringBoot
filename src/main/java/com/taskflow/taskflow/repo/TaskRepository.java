package com.taskflow.taskflow.repo;

import com.taskflow.taskflow.model.Task;
import com.taskflow.taskflow.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    Page<Task> findByUserAndCompleted(User user, boolean completed, Pageable pageable);
    @Query("SELECT t FROM Task t WHERE t.user = :user AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> searchTaskByKeyword(@Param("user") User user, @Param("keyword")String keyword ,Pageable pageable);

    List<Task> findByUserId(Long userId);
}
