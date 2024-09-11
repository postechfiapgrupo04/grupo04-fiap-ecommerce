package br.com.fiap.login.domain;

import br.com.fiap.login.application.dto.UserDTO;

public interface UserUsecase {

    UserDTO addUser(UserDTO userDTO);

    UserDTO getUserById(String id);

    UserDTO getUserByUsername(String username);

    UserDTO updateUser(UserDTO userDTO);

    void deleteUser(String userId);

    UserDTO me();
}
