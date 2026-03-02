package com.prerna.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int status;
    private String message;
    private T body;

    // Success response
    public static <T> ApiResponse<T> success(String message, T body) {
        return new ApiResponse<>(200, message, body);
    }

    // Created response
    public static <T> ApiResponse<T> created(String message, T body) {
        return new ApiResponse<>(201, message, body);
    }

    // Error response
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}