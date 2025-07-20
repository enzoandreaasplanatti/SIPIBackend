package org.example.sipibackend.security;

import lombok.RequiredArgsConstructor;
import org.example.sipibackend.entity.Token;
import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.dto.AuthRequest;
import org.example.sipibackend.entity.dto.UserDTO;
import org.example.sipibackend.repository.TokenRepository;
import org.example.sipibackend.repository.UserRepository;
import org.example.sipibackend.service.EmailService;
import org.example.sipibackend.service.TokenService;
import org.example.sipibackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email ya en uso");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setIsActive(false); // No activo hasta validar
        user.setActiveAccount(false);
        userRepository.save(user);

        // Generar token de 6 caracteres
        String tokenStr = generateTokenRegister(6);
        Token token = new Token();
        token.setToken(tokenStr);
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(token);

        // Enviar email
        emailService.sendTokenEmail(user.getEmail(), tokenStr);

        return ResponseEntity.ok("Usuario registrado. Verifica tu email para activar la cuenta.");
    }

    private String generateTokenRegister(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Email inválido"));

        if (!user.getIsActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La cuenta está desactivada");
        }

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "rol", user.getRole(),
                "email", user.getEmail(),
                "id", user.getId()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody UserDTO userDTO) {
        userService.logout(userDTO);
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }

    @PostMapping("/validateRegisterToken")
    public ResponseEntity<?> validateRegisterToken(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String tokenStr = payload.get("token");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        User user = userOpt.get();
        Optional<Token> tokenOpt = tokenService.findByToken(tokenStr);
        if (tokenOpt.isEmpty() || !tokenOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        Token token = tokenOpt.get();
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado");
        }
        //user.setIsActive(true);
        user.setActiveAccount(true);
        userRepository.save(user);
        tokenRepository.delete(token);
        return ResponseEntity.ok("Cuenta activada correctamente");
    }

    @PostMapping("/requestPasswordReset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        User user = userOpt.get();
        // Generar token de 6 caracteres
        String tokenStr = generateTokenRegister(6);
        Token token = new Token();
        token.setToken(tokenStr);
        token.setUser(user);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(token);
        // Enviar email
        emailService.sendTokenEmail(user.getEmail(), tokenStr);
        return ResponseEntity.ok("Se ha enviado un código de verificación a tu email");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String tokenStr = payload.get("token");
        String newPassword = payload.get("newPassword");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        User user = userOpt.get();
        Optional<Token> tokenOpt = tokenService.findByToken(tokenStr);
        if (tokenOpt.isEmpty() || !tokenOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        Token token = tokenOpt.get();
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado");
        }
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(token);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @PostMapping("/validatePasswordResetToken")
    public ResponseEntity<?> validatePasswordResetToken(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String tokenStr = payload.get("token");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        User user = userOpt.get();
        Optional<Token> tokenOpt = tokenService.findByToken(tokenStr);
        if (tokenOpt.isEmpty() || !tokenOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        Token token = tokenOpt.get();
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expirado");
        }
        return ResponseEntity.ok("Token válido");
    }
}
