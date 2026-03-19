package com.officeflow.repository;

import com.officeflow.domain.StoredToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<StoredToken, String> {

    // Questo è il metodo magico che useremo nel JwtFilter
    // Cercherà nel DB se la stringa inviata (anche una "X") esiste tra i token validi
    Optional<StoredToken> findByToken(String token);

    // Utile per invalidare tutti i token di un utente specifico
    void deleteByUserEmail(String email);
}