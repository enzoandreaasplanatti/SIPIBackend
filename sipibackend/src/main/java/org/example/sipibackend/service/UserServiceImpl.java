package org.example.sipibackend.service;

import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.UserDTO;
import org.example.sipibackend.repository.TokenRepository;
import org.example.sipibackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void createUser(UserDTO userDTO) {
        userRepository.save(userDTO.toEntity());
    }

    public void deactivateUser(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setActiveAccount(false);
            userRepository.save(user);
        }
    }

    public void logout(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(userDTO.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            tokenRepository.deleteByUser(user);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
