package br.com.fiap.gateway.filter;

import br.com.fiap.gateway.service.ValidateTokenService;
import br.com.fiap.gateway.service.dto.UserDTO;
import lombok.extern.java.Log;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Log
@Component
public class AuthUserFilter implements GatewayFilter {

    private final ValidateTokenService validateTokenService;

    public AuthUserFilter(ValidateTokenService validateTokenService) {
        this.validateTokenService = validateTokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        UserDTO userDTOMono = validateTokenService.validateToken(exchange.getRequest().getHeaders().getFirst("Authorization"));

        if(Objects.nonNull(userDTOMono)) {
            if (userDTOMono.authorities().stream().anyMatch(any -> any.authority().equals("ROLE_USER"))) {
                return chain.filter(exchange);
            }
        }
        return Mono.error(new RuntimeException("Usuário não possui permissão de acesso a essa rota!"));
    }

}

