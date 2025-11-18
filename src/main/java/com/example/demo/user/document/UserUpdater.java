package com.example.demo.user.document;

import lombok.Builder;

@Builder
public record UserUpdater(
        String name,
        Integer age,
        Boolean isActive
) {
}
