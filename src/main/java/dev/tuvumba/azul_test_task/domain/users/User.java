package dev.tuvumba.azul_test_task.domain.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table(name="users")
@Entity
public class User {

    @Id
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password; // in plaintext for now, easily moved to BCrypt
    @Enumerated(EnumType.STRING)
    private UserRole role;
    public User() {}
}

