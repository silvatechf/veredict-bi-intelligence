package com.ajh.auth.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"") // Aspas duplas pois 'user' é palavra reservada no Postgres
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // --- MÉTODOS OBRIGATÓRIOS DO USERDETAILS ---

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Padrão Spring Security: ROLE_CANDIDATE, ROLE_ADMIN, etc.
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // --- MÉTODOS DE CONTROLE DE ACESSO ---

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // --- GETTERS EXPLÍCITOS PARA O ADAPTER ---
    // (Ajuda se o Lombok falhar na compilação)

    public String getEmail() {
        return this.email;
    }

    public Role getRole() {
        return this.role;
    }
}