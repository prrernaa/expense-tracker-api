package com.prerna.expense_tracker.controller;

import com.prerna.expense_tracker.dto.ApiResponse;
import com.prerna.expense_tracker.dto.ExpenseRequest;
import com.prerna.expense_tracker.dto.ExpenseResponse;
import com.prerna.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created("Expense created successfully", expenseService.createExpense(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", expenseService.getAllExpenses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Expense fetched successfully", expenseService.getExpenseById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", expenseService.updateExpense(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> filter(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<ExpenseResponse> result = expenseService.filterExpenses(categoryId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Expenses filtered successfully", result));
    }
}