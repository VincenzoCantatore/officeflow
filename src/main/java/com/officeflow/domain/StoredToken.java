package com.officeflow.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens") // Nome della collezione su MongoDB
public class StoredToken {

    @Id
    private String id;

    @Indexed(unique = true) // Importante: rende la ricerca del token velocissima
    private String token;

    private String userEmail;

    private boolean revoked; // Se true, il token è stato annullato (es. dopo logout)

    private LocalDateTime createdAt; // Utile per pulizie periodiche del DB
}