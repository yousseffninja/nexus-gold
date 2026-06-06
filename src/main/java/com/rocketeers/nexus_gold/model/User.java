package com.rocketeers.nexus_gold.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rocketeers.nexus_gold.enums.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Roles role;

    @Column(
            name = "display_name",
            unique = true,
            nullable = false
    )
    private String displayName;

    @Builder.Default
    @Column(name = "email_verified", nullable = false, columnDefinition = "boolean default false")
    private boolean emailVerified = false;

    @JsonIgnore
    @Column(name = "email_verification_code")
    private String emailVerificationCode;

    @JsonIgnore
    @Column(name = "email_verification_code_expires_at")
    private LocalDateTime emailVerificationCodeExpiresAt;

    @JsonIgnore
    @Column(name = "password_Reset_code")
    private String passwordResetCode;

    @JsonIgnore
    @Column(name = "password_Reset_code_expires_at")
    private LocalDateTime passwordResetCodeExpiresAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}