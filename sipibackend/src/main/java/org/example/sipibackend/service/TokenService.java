package org.example.sipibackend.service;

import org.example.sipibackend.entity.Token;

import java.util.Optional;

public interface TokenService {
    Optional<Token> findByToken(String tokenStr);
}
