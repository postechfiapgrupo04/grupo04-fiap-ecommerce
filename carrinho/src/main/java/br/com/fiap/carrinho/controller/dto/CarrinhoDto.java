package br.com.fiap.carrinho.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CarrinhoDto {
    private String idUsuario;
    private Long totalItens;
    private BigDecimal valorTotalCarrinho;
    private List<ItemCarrinhoDto> itens;
}
