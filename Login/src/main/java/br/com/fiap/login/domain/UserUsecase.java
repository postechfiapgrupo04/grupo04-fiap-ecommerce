package br.com.fiap.login.domain;

import br.com.fiap.login.application.dto.UserDTO;

public interface UserUsecase {

    public UserDTO addUser(UserDTO userDTO);

    public UserDTO getUserByUsername(String username);

    public UserDTO updateUser(UserDTO userDTO);

    public void deleteUser(String userId);

    public UserDTO me();
}
