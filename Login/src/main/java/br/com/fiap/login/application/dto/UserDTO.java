package br.com.fiap.login.application.dto;

import java.util.List;

public record UserDTO(String id, String name, String email, List<UserAuthorityDTO> authorities, String password) {
}
