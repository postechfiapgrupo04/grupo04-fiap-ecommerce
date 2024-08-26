package br.com.fiap.login.application.converter;

import br.com.fiap.login.application.dto.UserAuthorityDTO;
import br.com.fiap.login.application.dto.UserDTO;
import br.com.fiap.login.domain.entity.User;
import br.com.fiap.login.domain.entity.UserAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    public UserDTO convertUserToUserDTO(User user) {
        return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getAuthorityList().stream().map(this::convertUserAuthorityToUserAuthorityDTO).toList());
    }

    private UserAuthorityDTO convertUserAuthorityToUserAuthorityDTO(UserAuthority userAuthority) {
        return new UserAuthorityDTO(userAuthority.getAuthorityId(), userAuthority.getAuthority());
    }
}
