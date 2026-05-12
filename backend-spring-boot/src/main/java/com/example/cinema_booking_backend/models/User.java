package com.example.cinema_booking_backend.models;

import com.example.cinema_booking_backend.enums.auth.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

// User entity for database mapping and security authentication
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phoneNumber;

    private String fullName;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    // Stores the long JWT string for session renewal
    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    // Persists the role name as a string in the database
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isVerified;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // // --- Spring Security Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Map the user's role to a GrantedAuthority for access control
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        // Assume account is always valid unless specified
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Assume account is not locked by default
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Password expiration is not implemented
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Assume the account is active
        return true;
    }

    // // --- JPA Lifecycle Hooks ---

    @PrePersist
    protected void onCreate() {
        // Set default values and timestamps before saving
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.ROLE_USER;
        }
        isVerified = false;
    }

    @PreUpdate
    protected void onUpdate() {
        // Update the timestamp on every record modification
        updatedAt = LocalDateTime.now();
    }

    // // --- Standard Getters and Setters ---

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        // Overrides UserDetails method
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        // Overrides UserDetails method
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        this.isVerified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}