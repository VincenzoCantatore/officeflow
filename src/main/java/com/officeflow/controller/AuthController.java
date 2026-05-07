package com.officeflow.controller;

import com.officeflow.config.JwtUtils;
import com.officeflow.domain.StoredToken;
import com.officeflow.domain.User;
import com.officeflow.dto.request.LoginRequestDTO;
import com.officeflow.dto.request.UserRequestDTO;
import com.officeflow.dto.response.JwtResponseDTO;
import com.officeflow.dto.response.UserResponseDTO;
import com.officeflow.repository.TokenRepository;
import com.officeflow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO request) {
        User user = toUser(request);
        return ResponseEntity.ok(toUserResponse(userService.createUser(user)));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        // 1. Autenticazione: controlla se email e password sono corretti
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. Generazione del Token JWT
        String jwt = jwtUtils.generateToken(authentication);
        User userDetails = (User) authentication.getPrincipal();

        // 3. SALVATAGGIO NEL DATABASE (Cruciale per il JwtFilter)
        try {
            StoredToken storedToken = StoredToken.builder()
                    .token(jwt)
                    .userEmail(userDetails.getEmail())
                    .revoked(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            tokenRepository.save(storedToken);
            log.info("Token salvato con successo nel DB per l'utente: {}", userDetails.getEmail());
        } catch (Exception e) {
            log.error("Impossibile salvare il token su MongoDB", e);
        }

        // 4. Risposta al client
        return ResponseEntity.ok(new JwtResponseDTO(
                jwt,
                userDetails.getEmail(),
                userDetails.getRole()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String jwt = parseJwt(request);
        if (jwt != null) {
            tokenRepository.findByToken(jwt).ifPresent(token -> {
                token.setRevoked(true);
                tokenRepository.save(token);
            });
        }
        return ResponseEntity.noContent().build();
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private User toUser(UserRequestDTO request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    private UserResponseDTO toUserResponse(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
