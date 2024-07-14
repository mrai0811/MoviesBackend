package com.rai.movieapi.auth.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "The name filed can't be blank")
    private String name;

    @NotBlank(message = "The username filed can't be blank")
    @Column(unique = true)
    private String username;
    @NotBlank(message = "The email filed can't be blank")
    @Column(unique = true)
    @Email(message = "Please enter email in proper fomat")
    private String email;
    @NotBlank(message = "The name filed can't be blank")
    @Size(min = 6, message = "The passport must contain 6 alphanumeric characters")
    private String password;

    @OneToOne(mappedBy = "user")
    private ForgetPassword forgetPassword;

    @OneToOne(mappedBy = "user")
    private RefreshToken refreshToken;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    private boolean isEnabled = true;
    private boolean isAccountNonExpired = true;
    private boolean isAccountNBonLocked = true;
    private boolean isCredentialsNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
