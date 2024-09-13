package br.com.fiap.gateway.service;

import br.com.fiap.gateway.service.dto.UserDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ValidateTokenService {

    public UserDTO validateToken(String token) {

        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<UserDTO> response = client.exchange("http://ms-login:8002/api/user/me", HttpMethod.GET, request, UserDTO.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
