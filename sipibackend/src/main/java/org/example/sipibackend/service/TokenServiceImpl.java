package org.example.sipibackend.service;

import org.example.sipibackend.entity.Token;
import org.example.sipibackend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public Optional<Token> findByToken(String tokenStr) {
        return tokenRepository.findByToken(tokenStr);
    }
}
