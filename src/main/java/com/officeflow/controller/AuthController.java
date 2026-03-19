package com.officeflow.controller;

import com.officeflow.config.JwtUtils;
import com.officeflow.domain.User;
import com.officeflow.dto.request.LoginRequestDTO;
import com.officeflow.dto.response.JwtResponseDTO;
import com.officeflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Usiamo il service che abbiamo modificato per criptare la password
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        // 1. Spring Security controlla se email e password (in chiaro)
        // corrispondono a quelle (criptate) nel DB
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. Se l'autenticazione fallisce, Spring lancia un'eccezione qui.
        // Se passa, generiamo il Token JWT
        String jwt = jwtUtils.generateToken(authentication);

        // 3. Recuperiamo l'utente loggato dal contesto di Spring
        User userDetails = (User) authentication.getPrincipal();

        // 4. Rispondiamo con il Token e i dettagli base
        return ResponseEntity.ok(new JwtResponseDTO(
                jwt,
                userDetails.getEmail(),
                userDetails.getRole()
        ));
    }
}