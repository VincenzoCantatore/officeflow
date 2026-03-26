package com.officeflow.service;

import com.officeflow.domain.User;
import com.officeflow.domain.enums.UserRole;
import com.officeflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Iniettiamo il "criptatore"

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email già registrata");
        }

        // criptatura
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if (user.getEmail().toLowerCase().endsWith("@officeflow.admin.com")) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }
        return userRepository.save(user);
        }

    public void deleteUser(String id) {

        userRepository.deleteById(id);
    }
}