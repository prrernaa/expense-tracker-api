package com.prerna.expense_tracker.controller;

import com.prerna.expense_tracker.dto.ApiResponse;
import com.prerna.expense_tracker.dto.ExpenseRequest;
import com.prerna.expense_tracker.dto.ExpenseResponse;
import com.prerna.expense_tracker.dto.PaginatedResponse;
import com.prerna.expense_tracker.service.ExcelExportService;
import com.prerna.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExcelExportService excelExportService;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(201)
                .body(ApiResponse.created("Expense created successfully", expenseService.createExpense(request)));
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAll() {
//        return ResponseEntity.ok(ApiResponse.success("Expenses fetched successfully", expenseService.getAllExpenses()));
//    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ExpenseResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return ResponseEntity.ok(expenseService.getAllExpensesPaginated(page, size, sortBy, sortDir));
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

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportToExcel() throws IOException, IOException {
        ByteArrayInputStream file = excelExportService.exportExpensesToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=expenses.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Expense fetched successfully", expenseService.getExpenseById(id)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getSummary() {
        return ResponseEntity.ok(expenseService.getExpenseSummary());
    }

}