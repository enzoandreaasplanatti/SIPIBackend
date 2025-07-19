package org.example.sipibackend.service;

import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.UserDTO;

public interface UserService {
    void createUser(UserDTO userDTO);
    void deactivateUser(UserDTO userDTO);
    void logout(UserDTO userDTO);

}
