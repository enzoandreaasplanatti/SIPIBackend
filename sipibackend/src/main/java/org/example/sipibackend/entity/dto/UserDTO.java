package org.example.sipibackend.entity.dto;

import org.example.sipibackend.entity.User;
import org.example.sipibackend.entity.User.Role;
import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private String surname;
    private String email;
    private String age;
    private String password;
    private Role role;
    private Boolean isActive;
    private Boolean activeAccount;

    public User toEntity() {
        User user = new User();
        user.setName(this.name);
        user.setSurname(this.surname);
        user.setEmail(this.email);
        user.setAge(this.age);
        user.setPassword(this.password);
        user.setRole(this.role);
        user.setIsActive(this.isActive);
        user.setActiveAccount(this.activeAccount);
        return user;
    }
}
