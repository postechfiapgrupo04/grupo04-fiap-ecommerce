package br.com.fiap.login.domain.entity;

import io.micrometer.common.lang.NonNull;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "user_authority")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private String authorityId;

    @Column(name = "authority")
    @NonNull
    private String authority;

    @NonNull
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAuthority that)) return false;
        return Objects.equals(authorityId, that.authorityId) && Objects.equals(authority, that.authority) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorityId, authority, user);
    }
}