package com.officeflow.controller;

import com.officeflow.domain.User;
import com.officeflow.dto.request.UserRequestDTO;
import com.officeflow.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.officeflow.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @GetMapping
    public List<UserResponseDTO> getUsers() {

        return userService.getAllUsers()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO request) {

        return toResponse(userService.createUser(toUser(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {

        userService.deleteUser(id);
    }

    private User toUser(UserRequestDTO request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
