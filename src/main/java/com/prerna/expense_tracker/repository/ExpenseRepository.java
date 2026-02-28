package com.prerna.expense_tracker.repository;

import com.prerna.expense_tracker.entity.Expense;
import com.prerna.expense_tracker.entity.User;
import com.prerna.expense_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    Optional<Expense> findByIdAndUser(Long id, User user);
    List<Expense> findByUserAndCategory(User user, Category category);
    List<Expense> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}