package br.com.fiap.login.application.converter;

import br.com.fiap.login.application.dto.UserAuthorityDTO;
import br.com.fiap.login.application.dto.UserDTO;
import br.com.fiap.login.domain.entity.User;
import br.com.fiap.login.domain.entity.UserAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public UserConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO convertUserToUserDTO(User user) {
        return new UserDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getAuthorityList().stream().map(this::convertUserAuthorityToUserAuthorityDTO).toList(), "");
    }

    public User convertUserDTOToUser(UserDTO userDTO) {
        User user = new User();
        user.setUserId(userDTO.id());
        user.setEmail(userDTO.email());
        user.setUsername(userDTO.name());
        user.setPassword(passwordEncoder.encode(userDTO.password()));
        user.setAuthorityList(userDTO.authorities().stream().map(userAuthorityDTO -> convertUserAuthorityDTOToUserAuthority(userAuthorityDTO, user)).toList());
        return user;
    }

    private UserAuthority convertUserAuthorityDTOToUserAuthority(UserAuthorityDTO userAuthorityDTO, User user) {
        UserAuthority userAuthority = new UserAuthority();
        userAuthority.setAuthority(userAuthorityDTO.authority());
        userAuthority.setUser(user);
        return userAuthority;
    }

    private UserAuthorityDTO convertUserAuthorityToUserAuthorityDTO(UserAuthority userAuthority) {
        return new UserAuthorityDTO(userAuthority.getAuthorityId(), userAuthority.getAuthority());
    }
}
