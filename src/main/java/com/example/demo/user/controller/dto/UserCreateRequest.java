package com.example.demo.user.controller.dto;

public record UserCreateRequest(
        String id,
        String name,
        Integer age,
        Boolean isActive
) {
}
