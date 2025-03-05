package com.laborganized.LabOrganized.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain alphanumeric values")
    @Column(unique = true)
    private String username;
    @NotBlank(message = "Name must not be empty")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ]([\s'-](?![\s'-])|[a-zA-ZÀ-ÿ])*[a-zA-ZÀ-ÿ]$", message = "Name can only contain letters, whitespaces, apostrophes and dashes")
    private String name;
    @NotBlank(message = "Password must not be empty")
    private String passwordHash;
    @Email(message = "Please enter a valid email")
    @NotEmpty(message = "Please enter a valid email")
    @Column(unique = true)
    private String email;
    @NotNull(message = "Date of creation must not be null")
    private LocalDateTime created;
    @NotNull(message = "Date of last update must not be null")
    private LocalDateTime lastUpdated;
    @NotNull(message = "User role must be defined")
    private String roles;
    @OneToMany(mappedBy = Storeable_.USER, cascade = CascadeType.REMOVE)
    private List<Storeable> storeableList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<Storeable> getStoreableList() {
        return storeableList;
    }

    public void setStoreableList(List<Storeable> storeableList) {
        this.storeableList = storeableList;
    }
}
