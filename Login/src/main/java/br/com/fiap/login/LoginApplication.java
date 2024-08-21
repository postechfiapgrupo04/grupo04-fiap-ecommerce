package br.com.fiap.login;

import br.com.fiap.login.config.RsaKeyConfigProperties;
import br.com.fiap.login.domain.entity.User;
import br.com.fiap.login.domain.entity.UserAuthority;
import br.com.fiap.login.domain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfigProperties.class)
public class LoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApplication.class, args);
    }

    @Bean
    public CommandLineRunner initializeUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            UserAuthority userAuthority = new UserAuthority();
            userAuthority.setAuthority("ROLE_USER");


            User user = new User();
            user.setUsername("exampleuser");
            user.setEmail("example@gmail.com");
            user.setPassword(passwordEncoder.encode("examplepassword"));
            userAuthority.setUser(user);
            user.setAuthorityList(List.of(userAuthority));

            // Save the user to the database
            userRepository.save(user);

        };
    }

}
