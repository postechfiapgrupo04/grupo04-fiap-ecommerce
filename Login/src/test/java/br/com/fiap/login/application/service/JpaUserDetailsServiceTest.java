package br.com.fiap.login.application.service;

import br.com.fiap.login.domain.repository.UserRepository;
import br.com.fiap.login.security.AuthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JpaUserDetailsServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        AuthUser user = new AuthUser();
        user.setUsername("userName");
        user.setPassword("password");
        when(userRepository.findByUsername(any())).thenReturn(java.util.Optional.of(user));

        // When
        JpaUserDetailsService userService = new JpaUserDetailsService(userRepository);
        AuthUser result = (AuthUser) userService.loadUserByUsername("userName");

        // Then
        assertNotNull(result);
        assertEquals("userName", result.getUsername());
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Given
        when(userRepository.findByUsername(any())).thenReturn(java.util.Optional.empty());

        // When
        JpaUserDetailsService userService = new JpaUserDetailsService(userRepository);

        // Then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("userName"));
    }
}