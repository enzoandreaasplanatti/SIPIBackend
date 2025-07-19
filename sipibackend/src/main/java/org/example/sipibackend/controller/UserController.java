package org.example.sipibackend.controller;

import org.example.sipibackend.entity.dto.UserDTO;
import org.example.sipibackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PatchMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestBody UserDTO userDTO) {
        userService.deactivateUser(userDTO);
        return ResponseEntity.ok("Cuenta desactivada exitosamente");
    }


}
