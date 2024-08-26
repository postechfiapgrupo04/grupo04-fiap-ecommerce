package br.com.fiap.login.application.controller;

import br.com.fiap.login.application.converter.UserConverter;
import br.com.fiap.login.application.dto.AuthDTO;
import br.com.fiap.login.application.dto.AuthResponseDTO;
import br.com.fiap.login.domain.entity.User;
import br.com.fiap.login.domain.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserController(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @GetMapping("/me")
    public ResponseEntity<?> login() throws IllegalAccessException {

        Optional<User> user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            return ResponseEntity.ok(userConverter.convertUserToUserDTO(user.get()));
        }
        return ResponseEntity.notFound().build();
    }
}
