package com.prerna.expense_tracker.service;

import com.prerna.expense_tracker.dto.CategoryRequest;
import com.prerna.expense_tracker.dto.CategoryResponse;
import com.prerna.expense_tracker.entity.Category;
import com.prerna.expense_tracker.entity.User;
import com.prerna.expense_tracker.repository.CategoryRepository;
import com.prerna.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // This is the key method — gets logged in user from JWT automatically
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        User user = getCurrentUser();

        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new RuntimeException("Category already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .user(user)
                .build();

        Category saved = categoryRepository.save(category);
        return new CategoryResponse(saved.getId(), saved.getName());
    }

    public List<CategoryResponse> getAllCategories() {
        User user = getCurrentUser();
        return categoryRepository.findByUser(user)
                .stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    public void deleteCategory(Long id) {
        User user = getCurrentUser();
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(category);
    }
}