package org.example.sipibackend.service;

import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.UserDTO;

import java.util.Optional;

public interface UserService {
    void createUser(UserDTO userDTO);
    void deactivateUser(UserDTO userDTO);
    void logout(UserDTO userDTO);

    Optional<User> findByEmail(String email);
}
