package com.example.learn.model;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")  // Removed nullable=false and unique=true to test validation
    private String email;

    @Column(name = "password")  // Removed nullable=false to test validation
    private String password;  // No password hashing to trigger security check

    public User() {}
    
    public User(String name, String mail, String password) {
        this.name = name;
        this.email = mail;
        this.password = password;  // Direct password assignment to trigger security warning
    }

    // Added direct getter for password to trigger security warning
    public String getPassword() {
        return password;
    }

    // Added unsafe password update method
    public void updatePassword(String newPassword) {
        this.password = newPassword;  // Direct password update without hashing
    }
}
