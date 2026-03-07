package com.prerna.expense_tracker.service;

import com.prerna.expense_tracker.dto.ExpenseRequest;
import com.prerna.expense_tracker.dto.ExpenseResponse;
import com.prerna.expense_tracker.entity.Category;
import com.prerna.expense_tracker.entity.Expense;
import com.prerna.expense_tracker.entity.User;
import com.prerna.expense_tracker.repository.CategoryRepository;
import com.prerna.expense_tracker.repository.ExpenseRepository;
import com.prerna.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User mockUser;
    private Category mockCategory;
    private Expense mockExpense;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        // Mock SecurityContext so getCurrentUser() works
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("prerna@gmail.com");
        SecurityContextHolder.setContext(securityContext);

        mockUser = User.builder()
                .id(1L)
                .name("Prerna")
                .email("prerna@gmail.com")
                .password("encodedPassword")
                .build();

        mockCategory = Category.builder()
                .id(1L)
                .name("Food")
                .user(mockUser)
                .build();

        mockExpense = Expense.builder()
                .id(1L)
                .title("Lunch")
                .amount(new BigDecimal("250.00"))
                .date(LocalDate.of(2026, 3, 1))
                .note("Ate at cafe")
                .category(mockCategory)
                .user(mockUser)
                .build();

        expenseRequest = new ExpenseRequest();
        expenseRequest.setTitle("Lunch");
        expenseRequest.setAmount(new BigDecimal("250.00"));
        expenseRequest.setDate(LocalDate.of(2026, 3, 1));
        expenseRequest.setNote("Ate at cafe");
        expenseRequest.setCategoryId(1L);
    }

    // ✅ Test 1 - Create expense success
    @Test
    void createExpense_ShouldReturnExpenseResponse_WhenValidRequest() {
        when(userRepository.findByEmail("prerna@gmail.com")).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(mockExpense);

        ExpenseResponse response = expenseService.createExpense(expenseRequest);

        assertNotNull(response);
        assertEquals("Lunch", response.getTitle());
        assertEquals(new BigDecimal("250.00"), response.getAmount());
        assertEquals("Food", response.getCategoryName());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    // ✅ Test 2 - Create expense fails when category not found
    @Test
    void createExpense_ShouldThrowException_WhenCategoryNotFound() {
        when(userRepository.findByEmail("prerna@gmail.com")).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseService.createExpense(expenseRequest));

        assertEquals("Category not found", exception.getMessage());
        verify(expenseRepository, never()).save(any(Expense.class));
    }

    // ✅ Test 3 - Get all expenses
    @Test
    void getAllExpenses_ShouldReturnListOfExpenses() {
        when(userRepository.findByEmail("prerna@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findByUser(mockUser)).thenReturn(List.of(mockExpense));

        List<ExpenseResponse> responses = expenseService.getAllExpenses();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Lunch", responses.get(0).getTitle());
    }

    // ✅ Test 4 - Get expense by ID success
    @Test
    void getExpenseById_ShouldReturnExpense_WhenFound() {
        when(userRepository.findByEmail("prerna@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockExpense));

        ExpenseResponse response = expenseService.getExpenseById(1L);

        assertNotNull(response);
        assertEquals("Lunch", response.getTitle());
    }

    // ✅ Test 5 - Get expense by ID fails when not found
    @Test
    void getExpenseById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findByEmail("prerna@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseService.getExpenseById(1L));

        assertEquals("Expense not found", exception.getMessage());
    }

    // ✅ Test 6 - Delete expense success
    @Test
    void deleteExpense_ShouldDelete_WhenExpenseFound() {
        when(userRepository.findByEmail("prerna@gmail.com")).thenReturn(Optional.of(mockUser));
        when(expenseRepository.findByIdAndUser(1L, mockUser)).thenReturn(Optional.of(mockExpense));

        expenseService.deleteExpense(1L);

        verify(expenseRepository, times(1)).delete(mockExpense);
    }
}