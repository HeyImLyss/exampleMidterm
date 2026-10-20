package org.example.lyssmidtermexample.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password_hash;

    @Column(nullable = false)
    private String salt; // Unique random salt for this user

    public UserInfo() {}

    public UserInfo(String username, String password_hash, String salt) {
        this.username = username;
        this.password_hash = password_hash;
        this.salt = salt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return password_hash; }
    public void setPasswordHash(String password_hash) { this.password_hash = password_hash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
}