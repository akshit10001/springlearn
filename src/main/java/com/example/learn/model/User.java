package com.example.learn.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Table(name = "users")
@Data
public class User {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Email(message = "Please provide a valid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    public User() {}
    
    public User(String name, String email, String rawPassword) {
        this.name = name;
        this.email = email;
        setPassword(rawPassword);
    }

    // Hide password from JSON serialization
    public String getPassword() {
        return "[PROTECTED]";
    }

    public void setPassword(String rawPassword) {
        if (rawPassword != null) {
            this.password = passwordEncoder.encode(rawPassword);
        }
    }

    public boolean checkPassword(String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
