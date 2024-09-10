package br.com.fiap.carrinho.service.impl;

import br.com.fiap.carrinho.controller.dto.CarrinhoDto;
import br.com.fiap.carrinho.controller.dto.ItemCarrinhoDto;
import br.com.fiap.carrinho.controller.dto.UsuarioDto;
import br.com.fiap.carrinho.exception.CustomException;
import br.com.fiap.carrinho.exception.ResourceNotFoundException;
import br.com.fiap.carrinho.model.ItemCarrinho;
import br.com.fiap.carrinho.repository.ItemCarrinhoRepository;
import br.com.fiap.carrinho.service.CarrinhoService;
import br.com.fiap.carrinho.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrinhoServiceImpl implements CarrinhoService {

    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final UsuarioService usuarioService;
    private final ModelMapper modelMapper;

    public CarrinhoDto listarItensDoUsuarioFormatado(String usuarioId) {

        List<ItemCarrinho> itens = listarItensDoUsuario(usuarioId);

        if (itens.isEmpty()) {
            throw new CustomException("O carrinho do usuário está vazio.");
        }

        BigDecimal valorTotalCarrinho = itens.stream()
                .map(ItemCarrinho::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarrinhoDto.builder()
                .valorTotalCarrinho(valorTotalCarrinho)
                .itens(itens.stream()
                        .map(item -> modelMapper.map(item, ItemCarrinhoDto.class)).collect(Collectors.toList()))
                .idUsuario(usuarioId)
                .totalItens((long) itens.size())
                .build();
    }

    private List<ItemCarrinho> listarItensDoUsuario(String usuarioId) {
        return itemCarrinhoRepository.findByIdUsuario(usuarioId);
    }

    public ItemCarrinhoDto adicionarItem(ItemCarrinhoDto itemDto, String idUsuario) {

        ItemCarrinho item = new ItemCarrinho();
        item.setIdProduto(itemDto.getIdProduto());
        item.setQuantidade(itemDto.getQuantidade());
        item.setPreco(itemDto.getPreco());
        item.setIdUsuario(idUsuario);

        // Calcular o valor total do item
        item.atualizarValorTotal();

        ItemCarrinho itemCarrinho = itemCarrinhoRepository.save(item);
        return modelMapper.map(itemCarrinho, ItemCarrinhoDto.class);
    }

    public void removerItem(String usuarioId, Long id) {
        Optional<ItemCarrinho> item = itemCarrinhoRepository.findByIdUsuarioAndId(usuarioId, id);
        if (item.isPresent()) {
            itemCarrinhoRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Item do carrinho não encontrado com o id: " + id);
        }
    }
}
