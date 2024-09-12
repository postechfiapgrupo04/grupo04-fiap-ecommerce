package br.com.fiap.carrinho.service;

import br.com.fiap.carrinho.controller.dto.CarrinhoDto;
import br.com.fiap.carrinho.controller.dto.ItemCarrinhoDto;

public interface CarrinhoService {
    CarrinhoDto listarItensDoUsuarioFormatado(String codigoUsuario);
    ItemCarrinhoDto adicionarItem(ItemCarrinhoDto itemDto, String usuarioId);
    void removerItem(String usuarioId, Long id);

}
