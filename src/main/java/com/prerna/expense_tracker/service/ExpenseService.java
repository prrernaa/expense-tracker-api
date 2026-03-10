package com.prerna.expense_tracker.service;

import com.prerna.expense_tracker.dto.ApiResponse;
import com.prerna.expense_tracker.dto.ExpenseRequest;
import com.prerna.expense_tracker.dto.ExpenseResponse;
import com.prerna.expense_tracker.dto.PaginatedResponse;
import com.prerna.expense_tracker.entity.*;
import com.prerna.expense_tracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getDate(),
                expense.getNote(),
                expense.getCategory().getName()
        );
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {
        User user = getCurrentUser();

        Category category = categoryRepository
                .findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Expense expense = Expense.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .date(request.getDate())
                .note(request.getNote())
                .category(category)
                .user(user)
                .build();

        return mapToResponse(expenseRepository.save(expense));
    }

//    public List<ExpenseResponse> getAllExpenses() {
//        return expenseRepository.findByUser(getCurrentUser())
//                .stream()
//                .map(this::mapToResponse)
//                .collect(Collectors.toList());
//    }

    public ExpenseResponse getExpenseById(Long id) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        User user = getCurrentUser();

        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        Category category = categoryRepository
                .findByIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setNote(request.getNote());
        expense.setCategory(category);

        return mapToResponse(expenseRepository.save(expense));
    }

    public void deleteExpense(Long id) {
        User user = getCurrentUser();
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expenseRepository.delete(expense);
    }

    public List<ExpenseResponse> filterExpenses(Long categoryId,
                                                LocalDate startDate,
                                                LocalDate endDate) {
        User user = getCurrentUser();
        List<Expense> expenses;

        if (categoryId != null && startDate != null && endDate != null) {
            // Filter by both category and date range
            expenses = expenseRepository
                    .findByUserAndCategoryIdAndDateBetween(user, categoryId, startDate, endDate);

        } else if (categoryId != null) {
            // Filter by category only
            expenses = expenseRepository.findByUserAndCategoryId(user, categoryId);

        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            expenses = expenseRepository.findByUserAndDateBetween(user, startDate, endDate);

        } else {
            // No filter — return all
            expenses = expenseRepository.findByUser(user);
        }

        return expenses.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApiResponse<PaginatedResponse<ExpenseResponse>> getAllExpensesPaginated(
            int page, int size, String sortBy, String sortDir) {

        User user = getCurrentUser();

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Expense> expensePage = expenseRepository.findByUser(user, pageable);

        List<ExpenseResponse> content = expensePage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        PaginatedResponse<ExpenseResponse> response = new PaginatedResponse<>(
                content,
                expensePage.getNumber(),
                expensePage.getTotalPages(),
                expensePage.getTotalElements(),
                expensePage.isLast()
        );

        return ApiResponse.success("Expenses fetched successfully", response);
    }

    public ApiResponse<Map<String, BigDecimal>> getExpenseSummary() {
        User user = getCurrentUser();
        List<Object[]> results = expenseRepository.getSummaryByCategory(user);

        Map<String, BigDecimal> summary = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Object[] row : results) {
            String categoryName = (String) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            summary.put(categoryName, amount);
            total = total.add(amount);
        }

        summary.put("Total", total);
        return ApiResponse.success("Summary fetched successfully", summary);
    }
}