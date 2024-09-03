package br.com.fiap.login.application.controller;

import br.com.fiap.login.application.dto.UserDTO;
import br.com.fiap.login.domain.UserUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    private final UserUsecase userUsecase;

    public UserController(UserUsecase userUsecase) {
        this.userUsecase = userUsecase;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(userUsecase.me());
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userUsecase.addUser(userDTO));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userUsecase.getUserByUsername(username));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userUsecase.updateUser(userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userUsecase.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
