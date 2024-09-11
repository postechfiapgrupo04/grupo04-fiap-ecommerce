package br.com.fiap.carrinho.service;

import br.com.fiap.carrinho.controller.dto.UsuarioDto;

import java.util.Optional;

public interface UsuarioService {
    Optional<UsuarioDto> obterUsuarioPorId(String usuarioId);
}
