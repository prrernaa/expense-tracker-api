package com.prerna.expense_tracker.repository;

import com.prerna.expense_tracker.entity.Expense;
import com.prerna.expense_tracker.entity.User;
import com.prerna.expense_tracker.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    Page<Expense> findByUser(User user, Pageable pageable);
    Optional<Expense> findByIdAndUser(Long id, User user);
    List<Expense> findByUserAndCategory(User user, Category category);

    List<Expense> findByUserAndCategoryId(User user, Long categoryId);
    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserAndCategoryIdAndDateBetween(User user, Long categoryId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT e.category.name, SUM(e.amount) FROM Expense e WHERE e.user = :user GROUP BY e.category.name")
    List<Object[]> getSummaryByCategory(@Param("user") User user);
}