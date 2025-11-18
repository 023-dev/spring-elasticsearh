package com.example.demo.user.controller;

import com.example.demo.user.controller.dto.UserCreateRequest;
import com.example.demo.user.controller.dto.UserUpdateRequest;
import com.example.demo.user.document.User;
import com.example.demo.user.repository.UserDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserDocumentRepository userDocumentRepository;

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestBody final UserCreateRequest request
    ) {
        final var userDocument = User.builder()
                .id(request.id())
                .age(request.age())
                .isActive(request.isActive())
                .name(request.name())
                .build();
        return ResponseEntity.ok(userDocumentRepository.save(userDocument));
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAll(
            @PageableDefault final PageableDefault pageable
    ) {
        final var users = userDocumentRepository.findAll(PageRequest.of(pageable.page(), pageable.size()));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getOne(
            @PathVariable("userId") final String userId
    ) {
        return userDocumentRepository.findById(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable("userId") final String userId,
            @RequestBody final UserUpdateRequest request
    ) {
        var user = userDocumentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        final var updater = user.toUpdater()
                .name(request.name())
                .age(request.age())
                .isActive(request.isActive())
                .build();
        user.update(updater);
        userDocumentRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable("userId") final String userId
    ) {
        final var user = userDocumentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userDocumentRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
}
