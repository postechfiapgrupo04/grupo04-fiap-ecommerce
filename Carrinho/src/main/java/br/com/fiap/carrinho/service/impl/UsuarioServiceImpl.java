package br.com.fiap.carrinho.service.impl;

import br.com.fiap.carrinho.controller.dto.UsuarioDto;
import br.com.fiap.carrinho.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
    @Override
    public Optional<UsuarioDto> obterUsuarioPorId(String usuarioId) {
        return Optional.of(UsuarioDto.builder().idUsuario("1").build());
    }
}
