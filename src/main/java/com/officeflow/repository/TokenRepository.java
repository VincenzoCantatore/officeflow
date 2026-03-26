package com.officeflow.repository;

import com.officeflow.domain.StoredToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends MongoRepository<StoredToken, String> {


    Optional<StoredToken> findByToken(String token);

}