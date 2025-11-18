package com.example.demo.user.controller.dto;

public record UserUpdateRequest(
        String name,
        Integer age,
        Boolean isActive
) {
}
