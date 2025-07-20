package org.example.sipibackend.repository;

import org.example.sipibackend.entity.Token;
import org.example.sipibackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    void deleteByUser(User user);

    Optional<Token> findByToken(String tokenStr);
}
