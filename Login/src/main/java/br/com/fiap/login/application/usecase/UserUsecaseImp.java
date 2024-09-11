package br.com.fiap.login.application.usecase;

import br.com.fiap.login.application.converter.UserConverter;
import br.com.fiap.login.application.dto.UserDTO;
import br.com.fiap.login.application.exceptions.AppException;
import br.com.fiap.login.domain.UserUsecase;
import br.com.fiap.login.domain.entity.User;
import br.com.fiap.login.domain.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserUsecaseImp implements UserUsecase {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserUsecaseImp(UserRepository userRepository, UserConverter userConverter) {
        this.userRepository = userRepository;
        this.userConverter = userConverter;
    }

    @Override
    public UserDTO addUser(UserDTO userDTO) {
        if (!userDTO.id().isEmpty()) {
            Optional<User> user = userRepository.findById(userDTO.id());
            if (user.isPresent()) {
                throw new AppException("Usuário já existente na base de dados");
            }
        }
        return userConverter.convertUserToUserDTO(userRepository.save(userConverter.convertUserDTOToUser(userDTO)));

    }

    @Override
    public UserDTO getUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new AppException("Usuário não encontrado na base de dados");
        }
        return userConverter.convertUserToUserDTO(user.get());
    }

    @Override
    public UserDTO getUserByUsername(String username) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new AppException("Usuário não encontrado na base de dados");
        }
        return userConverter.convertUserToUserDTO(user.get());
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        Optional<User> user = userRepository.findById(userDTO.id());
        if (user.isEmpty()) {
            throw new AppException("Usuário não encontrado na base de dados");
        }
        return userConverter.convertUserToUserDTO(userRepository.save(user.get()));
    }

    @Override
    public void deleteUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new AppException("Usuário não encontrado na base de dados");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDTO me() {

        Optional<User> user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.map(userConverter::convertUserToUserDTO).orElse(null);
    }
}
