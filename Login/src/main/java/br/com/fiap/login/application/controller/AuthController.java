package br.com.fiap.login.application.controller;

import br.com.fiap.login.application.dto.AuthDTO;
import br.com.fiap.login.application.dto.AuthResponseDTO;
import br.com.fiap.login.application.service.AuthService;
import br.com.fiap.login.domain.UserUsecase;
import br.com.fiap.login.security.AuthUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserUsecase userUsecase;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserUsecase userUsecase) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userUsecase = userUsecase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO userLogin) throws IllegalAccessException {
        Authentication authentication =
                authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                userLogin.username(),
                                userLogin.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Token requested for user :{}", authentication.getAuthorities());
        String token = authService.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponseDTO("Usuário logado com sucesso", token));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String id) {
        return ResponseEntity.ok(userUsecase.getUserById(id));
    }


}

