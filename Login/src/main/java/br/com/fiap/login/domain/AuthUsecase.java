package br.com.fiap.login.domain;


import br.com.fiap.login.application.dto.AuthResponseDTO;
import br.com.fiap.login.application.dto.UserDTO;

public interface AuthUsecase {

    public AuthResponseDTO login(String username, String password);

    public UserDTO getCurrentUser();
}
