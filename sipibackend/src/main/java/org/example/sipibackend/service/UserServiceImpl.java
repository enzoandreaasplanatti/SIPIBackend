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

    @Override
    public void updateUserInfo(String email, UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (userDTO.getName() != null) user.setName(userDTO.getName());
            if (userDTO.getSurname() != null) user.setSurname(userDTO.getSurname());
            if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
            if (userDTO.getAge() != null) user.setAge(userDTO.getAge());
            if (userDTO.getPassword() != null) user.setPassword(userDTO.getPassword());
            if (userDTO.getIsActive() != null) user.setIsActive(userDTO.getIsActive());
            if (userDTO.getActiveAccount() != null) user.setActiveAccount(userDTO.getActiveAccount());
            // No se permite modificar el rol
            userRepository.save(user);
        }
    }

    @Override
    public Optional<User> findById(Long publisherId) {
        if (publisherId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return userRepository.findById(publisherId);
    }

}
