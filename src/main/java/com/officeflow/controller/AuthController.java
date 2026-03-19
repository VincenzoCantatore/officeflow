package com.officeflow.controller;

import com.officeflow.config.JwtUtils;
import com.officeflow.domain.User;
import com.officeflow.domain.StoredToken; // Importa il nuovo model
import com.officeflow.dto.request.LoginRequestDTO;
import com.officeflow.dto.response.JwtResponseDTO;
import com.officeflow.repository.TokenRepository; // Importa il nuovo repository
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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository; // Aggiunto per gestire il database dei token

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        // 1. Autenticazione standard email/password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. Generazione del Token JWT
        String jwt = jwtUtils.generateToken(authentication);

        // 3. Recupero dettagli utente
        User userDetails = (User) authentication.getPrincipal();

        // --- NUOVA LOGICA: SALVATAGGIO NEL DATABASE ---
        // Prima di rispondere, salviamo il token nel DB per renderlo "ufficiale"
        StoredToken storedToken = StoredToken.builder()
                .token(jwt)
                .userEmail(userDetails.getEmail())
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(storedToken);
        // ----------------------------------------------

        // 4. Risposta al client
        return ResponseEntity.ok(new JwtResponseDTO(
                jwt,
                userDetails.getEmail(),
                userDetails.getRole()
        ));
    }
}